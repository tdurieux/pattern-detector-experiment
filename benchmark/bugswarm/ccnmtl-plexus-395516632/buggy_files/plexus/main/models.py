from datetime import datetime, timedelta

from django.contrib.auth.models import User
from django.db import models
from django.utils.encoding import python_2_unicode_compatible, smart_text

from plexus.grainlog.models import GrainLog


@python_2_unicode_compatible
class Location(models.Model):
    name = models.CharField(max_length=256)
    details = models.TextField(blank=True, default=u"")

    def __str__(self):
        return self.name

    def get_absolute_url(self):
        return "/location/%d/" % self.id


@python_2_unicode_compatible
class OSFamily(models.Model):
    name = models.CharField(max_length=256)

    class Meta:
        ordering = ['name', ]

    def __str__(self):
        return self.name

    def get_absolute_url(self):
        return "/os/%d/" % self.id


@python_2_unicode_compatible
class OperatingSystem(models.Model):
    family = models.ForeignKey(OSFamily)
    version = models.CharField(max_length=256)

    class Meta:
        ordering = ['version', ]

    def __str__(self):
        return smart_text(self.family) + " " + self.version

    def get_absolute_url(self):
        return "/os/%d/%d/" % (self.family.id, self.id)


@python_2_unicode_compatible
class Server(models.Model):
    name = models.CharField(max_length=256)
    primary_function = models.TextField(blank=True, default=u"")
    location = models.ForeignKey(Location, null=True, default="")
    operating_system = models.ForeignKey(OperatingSystem)
    memory = models.CharField(max_length=256, blank=True)
    disk = models.CharField(max_length=256, blank=True)
    swap = models.CharField(max_length=256, blank=True)
    notes = models.TextField(blank=True, default=u"")
    deprecated = models.BooleanField(default=False)
    graphite_name = models.CharField(max_length=256, default=u"", blank=True)
    ec2_instance_id = models.TextField(blank=True, default=u"")

    class Meta:
        ordering = ['name', ]

    def __str__(self):
        return self.name

    def get_absolute_url(self):
        return "/server/%d/" % self.id

    def add_contacts(self, contacts):
        for c in contacts:
            contact, created = Contact.objects.get_or_create(name=c)
            ServerContact.objects.create(server=self,
                                         contact=contact)

    def set_contacts(self, contacts):
        self.servercontact_set.all().delete()
        self.add_contacts(contacts)

    def ipaddress_default(self, ipaddress_id):
        if ipaddress_id:
            return IPAddress.objects.get(id=ipaddress_id)
        return self.ipaddress_set.all()[0]

    def server_notes(self):
        return [
            sn.note
            for sn in self.servernote_set.all().order_by("-note__created")]

    def grain_info(self):
        cg = GrainLog.objects.current_grainlog()
        if cg is None:
            return None
        g = cg.grain()
        s = g.server(self.name)
        if s is None:
            # try again with full hostname
            s = g.server(self.name + ".ccnmtl.columbia.edu")
        return s

    def contacts(self):
        return [ac.contact for ac in self.servercontact_set.all(
        ).select_related('contact')]

    def aliases(self):
        return Alias.objects.filter(ip_address__server=self)


@python_2_unicode_compatible
class IPAddress(models.Model):
    ipv4 = models.CharField(max_length=256)
    mac_addr = models.CharField(max_length=256, null=True, blank=True)
    server = models.ForeignKey(Server)

    def __str__(self):
        return self.ipv4


@python_2_unicode_compatible
class Contact(models.Model):
    name = models.CharField(max_length=256)
    email = models.CharField(max_length=256, default="")
    phone = models.CharField(max_length=256, default="")

    def __str__(self):
        return self.name

    def get_absolute_url(self):
        return "/contact/%d/" % self.id

    def active_servers(self):
        return [ac.server for ac in self.servercontact_set.filter(
            server__deprecated=False)]

    def active_applications(self):
        return [ac.application for ac in self.applicationcontact_set.filter(
            application__deprecated=False)]


@python_2_unicode_compatible
class Alias(models.Model):
    hostname = models.CharField(max_length=256)
    ip_address = models.ForeignKey(IPAddress, null=True)
    status = models.CharField(
        max_length=256, default=u"active",
        choices=[("active", "active"), ("pending", "pending"),
                 ("deprecated", "deprecated")]
    )
    description = models.TextField(blank=True, default=u"")
    administrative_info = models.TextField(
        blank=True,
        default=u"",
        help_text=("Required if not a .ccnmtl.columbia.edu hostname."
                   "Please use this field for information about "
                   "where the domain is registered, what account "
                   "it's set up with (don't enter passwords here though) "
                   "and who handles payments, DNS changes, etc."))

    class Meta:
        ordering = ['hostname', ]

    def __str__(self):
        return self.hostname

    def status_css_class(self):
        if self.status == 'pending':
            return "warning"
        if self.status == 'deprecated':
            return "error"
        return ""

    def is_deprecated(self):
        return self.status == 'deprecated'

    def can_request_dns_change(self):
        """ it's not safe for Plexus to try to request DNS changes
        outside our subdomain"""
        return (
            str(self.hostname).endswith(".ccnmtl.columbia.edu") or
            str(self.hostname).endswith(".ctl.columbia.edu"))

    def dns_change_request_email_subject(self):
        return "DNS Alias Change Request: " + self.hostname

    def dns_request_email_subject(self):
        return "DNS Alias Request: " + self.hostname

    def dns_request_email_body(self, r):
        return """
Please add the following alias:

      %s

It should resolve to %s (%s)

Thanks,
%s
""" % (self.hostname, self.ip_address.server.name, self.ip_address.ipv4, r)

    def dns_change_request_email_body(self, c_server, c_ip_address, r):
        h = self.hostname
        name = c_server.name
        ipv4 = c_ip_address.ipv4
        return """
Please change the following alias:

    %s

Which currently is an alias for %s (%s).

It should be changed to instead point to %s (%s).

Thanks,
%s
""" % (h, name, ipv4, self.ip_address.server.name, self.ip_address.ipv4, r)

    def get_absolute_url(self):
        return "/alias/%d/" % self.id


@python_2_unicode_compatible
class Technology(models.Model):
    name = models.CharField(max_length=256)

    def __str__(self):
        return self.name


@python_2_unicode_compatible
class Application(models.Model):
    name = models.CharField(max_length=256)
    description = models.TextField(blank=True, default=u"")
    technology = models.ForeignKey(Technology, null=True)
    graphite_name = models.CharField(max_length=256, default=u"", blank=True)
    sentry_name = models.CharField(max_length=256, default=u"", blank=True)
    pmt_id = models.IntegerField(default=0)
    deprecated = models.BooleanField(default=False)
    repo = models.TextField(default=u"", blank=True)
    github_url = models.TextField(default=u"", blank=True)

    # renewals
    # google analytics

    class Meta:
        ordering = ['name', ]

    def __str__(self):
        return self.name

    def pmt_feed_url(self):
        return ("https://pmt.ctl.columbia.edu/feeds/project/%d/"
                % self.pmt_id)

    def add_contacts(self, contacts):
        for c in contacts:
            contact, created = Contact.objects.get_or_create(name=c)
            ApplicationContact.objects.create(application=self,
                                              contact=contact)

    def set_contacts(self, contacts):
        self.applicationcontact_set.all().delete()
        self.add_contacts(contacts)

    def get_absolute_url(self):
        return "/application/%d/" % self.id

    def application_notes(self):
        return [
            sn.note
            for sn in self.applicationnote_set.all().order_by(
                "-note__created")]

    def contacts(self):
        return [ac.contact for ac in self.applicationcontact_set.all(
        ).select_related('contact')]

    def valid_renewal(self):
        now = datetime.now()
        return self.lease_set.filter(start__lte=now, end__gte=now).exists()

    def current_renewal(self):
        now = datetime.now()
        return self.lease_set.filter(start__lte=now, end__gte=now).first()

    def servers(self):
        d = {'staging': [], 'production': [], 'dev': []}
        g = GrainLog.objects.current_grainlog()
        if not g:
            return d

        for app in g.grain().by_app():
            if app['app'] == self.graphite_name:
                for server in app['servers']:
                    s = Server.objects.get(graphite_name=server.d['id'])
                    d[server.d['environment']].append(s)
        return d


class Lease(models.Model):
    application = models.ForeignKey(Application)
    start = models.DateField(auto_now_add=True)
    end = models.DateField()
    user = models.ForeignKey(User)
    notes = models.TextField(blank=True, default=u"")

    def upcoming(self):
        # is it coming up within the next month?
        return (datetime.now() + timedelta(weeks=4)).date() > self.end


@python_2_unicode_compatible
class ApplicationAlias(models.Model):
    application = models.ForeignKey(Application)
    alias = models.ForeignKey(Alias)

    def __str__(self):
        return smart_text(self.application) + " -> " + smart_text(self.alias)


@python_2_unicode_compatible
class ApplicationContact(models.Model):
    application = models.ForeignKey(Application)
    contact = models.ForeignKey(Contact)

    class Meta:
        order_with_respect_to = 'application'

    def __str__(self):
        return smart_text(self.application) + ": " + smart_text(self.contact)


@python_2_unicode_compatible
class ServerContact(models.Model):
    server = models.ForeignKey(Server)
    contact = models.ForeignKey(Contact)

    class Meta:
        order_with_respect_to = 'server'

    def __str__(self):
        return smart_text(self.server) + ": " + smart_text(self.contact)


class Note(models.Model):
    user = models.ForeignKey(User)
    created = models.DateTimeField(auto_now_add=True)
    body = models.TextField(blank=True, default=u"")


class ServerNote(models.Model):
    server = models.ForeignKey(Server)
    note = models.ForeignKey(Note)


class ApplicationNote(models.Model):
    application = models.ForeignKey(Application)
    note = models.ForeignKey(Note)
