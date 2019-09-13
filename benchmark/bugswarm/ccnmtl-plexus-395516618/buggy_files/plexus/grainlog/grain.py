""" some convenience classes to make it easier to work with
grain data as python objects instead of unwieldy json dicts """

from collections import defaultdict
import json


class Grain(object):
    def __init__(self, d):
        self.d = d
        self._build_indices()

    def _build_indices(self):
        self._server_idx = dict()
        self._app_idx = defaultdict(list)
        self._proxy_idx = defaultdict(list)
        self._roles_idx = defaultdict(list)

        for s in self.d.get('servers', []):
            server_name = list(s.keys())[0]
            data = s[server_name]
            if not isinstance(data, dict):
                # sometimes we get a string like
                # "Minion did not return. [Not connected]"
                # if salt can't talk to the minion at the moment
                # omit that one from the list
                continue
            server = Server(server_name, data)
            self._server_idx[server_name] = server

            self._index_server_apps(server)
            self._index_server_proxies(server)
            self._index_server_roles(server)

    def _index_server_apps(self, server):
        for a in server.apps():
            self._app_idx[a].append(server)

    def _index_server_proxies(self, server):
        for p in server.proxy():
            self._proxy_idx[p].append(server)

    def _index_server_roles(self, server):
        for r in server.roles():
            self._roles_idx[r].append(server)

    def servers(self):
        return sorted(self._server_idx.values(), key=lambda x: x.name)

    def server(self, server_name):
        return self._server_idx.get(server_name, None)

    def apps(self):
        return sorted(self._app_idx.keys())

    def proxies(self):
        return sorted(self._proxy_idx.keys())

    def roles(self):
        return sorted(self._roles_idx.keys())

    def app(self, app_name):
        """ return list of Servers running the specified app """
        return sorted(self._app_idx.get(app_name, []), key=lambda x: x.name)

    def proxy(self, app_name):
        """ return list of Servers proxying the specified app """
        return sorted(self._proxy_idx.get(app_name, []), key=lambda x: x.name)

    def role(self, role_name):
        """ return list of Servers implementing the specified role"""
        return sorted(self._roles_idx.get(role_name, []), key=lambda x: x.name)

    def by_app(self):
        """ return servers grouped by app (handy for template display) """
        for app in self.apps():
            yield dict(app=app, servers=self.app(app))

    def by_proxy(self):
        """ return servers grouped by proxy (handy for template display) """
        for p in self.proxies():
            yield dict(proxy=p, servers=self.proxy(p))

    def by_role(self):
        """ return servers grouped by role (handy for template display) """
        for r in self.roles():
            yield dict(role=r, servers=self.role(r))


class Server(object):
    def __init__(self, name, d):
        self.name = name
        self.d = d

    def keys(self):
        return self.d.keys()

    def apps(self):
        return self.d.get('apps', [])

    def proxy(self):
        return self.d.get('proxy', [])

    def roles(self):
        return self.d.get('roles', [])

    def json(self):
        return json.dumps(self.d, sort_keys=True, indent=4)
