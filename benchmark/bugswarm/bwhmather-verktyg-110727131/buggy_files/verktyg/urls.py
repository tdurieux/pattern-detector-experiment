# -*- coding: utf-8 -*-
"""
    werkzeug.urls
    ~~~~~~~~~~~~~

    :copyright: (c) 2014 by the Werkzeug Team, see AUTHORS for more details.
    :license: BSD, see LICENSE for more details.




"""
from urllib.parse import (
    urlsplit, urlunsplit,
    quote as urlquote, quote_plus as urlquote_plus,
)

from werkzeug._compat import to_unicode, to_native
from werkzeug._internal import _encode_idna, _decode_idna


_hexdigits = '0123456789ABCDEFabcdef'
_hextobyte = dict(
    ((a + b).encode(), int(a + b, 16))
    for a in _hexdigits for b in _hexdigits
)


def _safe_urlunquote_to_bytes(string, unsafe=''):
    if isinstance(string, str):
        string = string.encode('utf-8')
    if isinstance(unsafe, str):
        unsafe = unsafe.encode('utf-8')
    unsafe = frozenset(bytearray(unsafe))
    bits = iter(string.split(b'%'))
    result = bytearray(next(bits, b''))
    for item in bits:
        try:
            char = _hextobyte[item[:2]]
            if char in unsafe:
                raise KeyError()
            result.append(char)
            result.extend(item[2:])
        except KeyError:
            result.extend(b'%')
            result.extend(item)
    return bytes(result)


def _safe_urlunquote(string, charset='utf-8', errors='replace', unsafe=''):
    """URL decode a single string with a given encoding.  If the charset
    is set to `None` no unicode decoding is performed and raw bytes
    are returned.

    :param s:
        The string to unquote.
    :param charset:
        The charset of the query string.  If set to `None` no unicode decoding
        will take place.
    :param errors:
        The error handling for the charset decoding.
    """
    rv = _safe_urlunquote_to_bytes(string, unsafe)
    if charset is not None:
        rv = rv.decode(charset, errors)
    return rv


def url_fix(s, charset='utf-8'):
    r"""Sometimes you get an URL by a user that just isn't a real URL because
    it contains unsafe characters like ' ' and so on. This function can fix
    some of the problems in a similar way browsers handle data entered by the
    user:

    >>> url_fix(u'http://de.wikipedia.org/wiki/Elf (Begriffskl\xe4rung)')
    'http://de.wikipedia.org/wiki/Elf%20(Begriffskl%C3%A4rung)'

    :param s:
        The string with the URL to fix.
    :param charset:
        The target charset for the URL if the url was given as unicode string.
    """
    # First step is to convert backslashes (which are invalid in URLs anyways)
    # to slashes.  This is consistent with what Chrome does.
    s = s.replace('\\', '/')

    # For the specific case that we look like a malformed windows URL
    # we want to fix this up manually:
    if (
        s.startswith('file://') and
        s[7:8].isalpha() and
        s[8:10] in (':/', '|/')
    ):
        s = 'file:///' + s[7:]

    url = urlsplit(s)
    path = urlquote(
        url.path, encoding=charset, safe='/%+$!*\'(),'
    )
    qs = urlquote_plus(
        url.query, encoding=charset, safe=':&%=+$!*\'(),'
    )
    anchor = urlquote_plus(
        url.fragment, encoding=charset, safe=':&%=+$!*\'(),'
    )
    return urlunsplit(
        (url.scheme, url.encode_netloc(), path, qs, anchor)
    )


def uri_to_iri(uri, charset='utf-8', errors='replace'):
    r"""Converts a URI in a given charset to a IRI.

    Examples for URI versus IRI:

    >>> uri_to_iri(b'http://xn--n3h.net/')
    u'http://\u2603.net/'
    >>> uri_to_iri(b'http://%C3%BCser:p%C3%A4ssword@xn--n3h.net/p%C3%A5th')
    u'http://\xfcser:p\xe4ssword@\u2603.net/p\xe5th'

    Query strings are left unchanged:

    >>> uri_to_iri('/?foo=24&x=%26%2f')
    u'/?foo=24&x=%26%2f'

    :param uri:
        The URI to convert.
    :param charset:
        The charset of the URI.
    :param errors:
        The error handling on decode.
    """
    assert isinstance(uri, str)
    uri = urlsplit(to_unicode(uri, charset))

    host = _decode_idna(uri.hostname) if uri.hostname else ''
    if ':' in host:
        host = '[%s]' % host

    netloc = host

    if uri.port:
        if not 0 <= int(uri.port) <= 65535:
            raise ValueError('Invalid port')
        netloc = '%s:%s' % (netloc, uri.port)

    if uri.username or uri.password:
        if uri.username:
            username = _safe_urlunquote(
                uri.username, charset='utf-8', errors='strict', unsafe='/:%'
            )
        else:
            username = ''

        if uri.password:
            password = _safe_urlunquote(
                uri.password, charset='utf-8', errors='strict', unsafe='/:%'
            )
            auth = '%s:%s' % (username, password)
        else:
            auth = username

        netloc = '%s@%s' % (auth, netloc)

    path = _safe_urlunquote(
        uri.path, charset=charset, errors=errors, unsafe='%/;?'
    )
    query = _safe_urlunquote(
        uri.query, charset=charset, errors=errors, unsafe='%;/?:@&=+,$#'
    )
    fragment = _safe_urlunquote(
        uri.fragment, charset=charset, errors=errors, unsafe='%;/?:@&=+,$#'
    )
    return urlunsplit(
        (uri.scheme, netloc, path, query, fragment)
    )


def iri_to_uri(iri, errors='strict', safe_conversion=False):
    r"""Converts any unicode based IRI to an acceptable ASCII URI. Werkzeug always
    uses utf-8 URLs internally because this is what browsers and HTTP do as
    well. In some places where it accepts an URL it also accepts a unicode IRI
    and converts it into a URI.

    Examples for IRI versus URI:

    >>> iri_to_uri(u'http://☃.net/')
    'http://xn--n3h.net/'
    >>> iri_to_uri(u'http://üser:pässword@☃.net/påth')
    'http://%C3%BCser:p%C3%A4ssword@xn--n3h.net/p%C3%A5th'

    There is a general problem with IRI and URI conversion with some
    protocols that appear in the wild that are in violation of the URI
    specification.  In places where Werkzeug goes through a forced IRI to
    URI conversion it will set the `safe_conversion` flag which will
    not perform a conversion if the end result is already ASCII.  This
    can mean that the return value is not an entirely correct URI but
    it will not destroy such invalid URLs in the process.

    As an example consider the following two IRIs::

        magnet:?xt=uri:whatever
        itms-services://?action=download-manifest

    The internal representation after parsing of those URLs is the same
    and there is no way to reconstruct the original one.  If safe
    conversion is enabled however this function becomes a noop for both of
    those strings as they both can be considered URIs.

    :param iri:
        The IRI to convert.
    :param charset:
        The charset for the URI.
    :param safe_conversion:
        Indicates if a safe conversion should take place.  For more information
        see the explanation above.
    """
    assert isinstance(iri, str)
    if safe_conversion:
        try:
            native_iri = to_native(iri)
            ascii_iri = to_native(iri).encode('ascii')
            if ascii_iri.split() == [ascii_iri]:
                return native_iri
        except UnicodeError:
            pass

    iri = urlsplit(iri)

    host = _encode_idna(iri.hostname).decode('ascii') if iri.hostname else ''
    if ':' in host:
        host = '[%s]' % host

    netloc = host

    if iri.port:
        if not 0 <= int(iri.port) <= 65535:
            raise ValueError('Invalid port')
        netloc = '%s:%s' % (netloc, iri.port)

    if iri.username or iri.password:
        if iri.username:
            username = urlquote(
                iri.username, safe='/:%'
            )
        else:
            username = ''

        if iri.password:
            password = urlquote(
                iri.password, safe='/:%'
            )
            auth = '%s:%s' % (username, password)
        else:
            auth = username

        netloc = '%s@%s' % (auth, netloc)

    path = urlquote(
        iri.path, safe='/:~+%'
    )
    query = urlquote(
        iri.query, safe='%&[]:;$*()+,!?*/='
    )
    fragment = urlquote(
        iri.fragment, safe='=%&[]:;$()+,!?*/'
    )

    return urlunsplit(
        (iri.scheme, netloc, path, query, fragment)
    )
