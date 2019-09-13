from abjad.tools.abctools.AbjadValueObject import AbjadValueObject


class SystemBreak(AbjadValueObject):
    r'''System break indicator.

    ..  container:: example

        Default system break:

        >>> staff = abjad.Staff("c'4 d'4 e'4 f'4")
        >>> break_ = abjad.SystemBreak()
        >>> abjad.attach(break_, staff[-1])
        >>> abjad.show(staff) # doctest: +SKIP

        ..  docs::

            >>> abjad.f(staff)
            \new Staff {
                c'4
                d'4
                e'4
                f'4
                \break
            }

    '''

    ### CLASS VARIABLES ###

    __slots__ = (
        '_context',
        '_tag',
        )

    _format_slot = 'closing'

    _time_orientation = Right

    ### INITIALIZER ##

    def __init__(self, tag=None):
        self._context = 'Staff'
        if tag is not None:
            assert isinstance(tag, str), repr(tag)
        self._tag = tag

    ### PRIVATE METHODS ###

    def _get_lilypond_format(self):
        string = r'\break'
        if self.tag is not None:
            string += f' % {self.tag}'
        return string

    def _get_lilypond_format_bundle(self, component=None):
        import abjad
        bundle = abjad.LilyPondFormatBundle()
        bundle.after.commands.append(self._get_lilypond_format())
        return bundle

    ### PUBLIC PROPERTIES ###

    @property
    def context(self):
        r'''Gets default context of system break indicator.

        ..  container:: example

            Default system break:

            >>> break_ = abjad.SystemBreak()
            >>> break_.context
            'Staff'

        ..  todo:: Make system breaks score-contexted.

        Returns staff (but should return score).

        Returns context or string.
        '''
        return self._context

    @property
    def tag(self):
        r'''Gets tag.
        '''
        return self._tag
