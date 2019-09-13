from django import template

from plexus.main.models import Application, Alias


register = template.Library()


@register.simple_tag
def app_by_graphite_name(name):
    try:
        return Application.objects.get(graphite_name=name)
    except Application.DoesNotExist:
        return None


@register.simple_tag
def aliases(name):
    try:
        return Alias.objects.filter(
            ip_address__server__name=name).exclude(status='deprecated')
    except Alias.DoesNotExist:
        return None
    except Application.MultipleObjectsReturned:
        return None


@register.simple_tag
def server_grain(grains, server):
    try:
        grain = list(filter(lambda s: s.name == server, grains))[0]
        return grain
    except IndexError:
        return None
