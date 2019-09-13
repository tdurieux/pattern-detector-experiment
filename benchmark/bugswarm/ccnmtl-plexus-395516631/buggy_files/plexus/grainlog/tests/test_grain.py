import unittest

from plexus.grainlog.grain import Grain, Server


class GrainTest(unittest.TestCase):
    def test_servers(self):
        d = {
            'servers': [
                {'foo': {}},
                {'bar': {}},
            ]
        }
        g = Grain(d)
        servers = list(g.servers())
        self.assertEqual(len(servers), 2)

    def test_server(self):
        d = {
            'servers': [
                {'foo': {}},
                {'bar': {}},
            ]
        }
        g = Grain(d)
        s = g.server('foo')
        self.assertEqual(s.name, 'foo')

    def test_server_string(self):
        d = {
            'servers': [
                {'foo': {}},
                {'bar': "not a dict"},
            ]
        }
        g = Grain(d)
        s = g.server('foo')
        self.assertEqual(s.name, 'foo')
        self.assertTrue("bar" not in g.servers())

    def test_app(self):
        d = {
            'servers': [
                {'foo': {'apps': ['one']}},
                {'bar': {}},
            ]
        }
        g = Grain(d)
        a = g.app('one')
        self.assertEqual(len(a), 1)
        self.assertEqual(a[0].name, 'foo')

    def test_proxy(self):
        d = {
            'servers': [
                {'foo': {'proxy': ['one']}},
                {'bar': {}},
            ]
        }
        g = Grain(d)
        a = g.proxy('one')
        self.assertEqual(len(a), 1)
        self.assertEqual(a[0].name, 'foo')

    def test_role(self):
        d = {
            'servers': [
                {'foo': {'roles': ['one']}},
                {'bar': {}},
            ]
        }
        g = Grain(d)
        a = g.role('one')
        self.assertEqual(len(a), 1)
        self.assertEqual(a[0].name, 'foo')

    def test_apps(self):
        d = {
            'servers': [
                {'foo': {'apps': ['one']}},
                {'bar': {}},
            ]
        }
        g = Grain(d)
        self.assertEqual(g.apps(), ['one'])

    def test_proxies(self):
        d = {
            'servers': [
                {'foo': {'proxy': ['one']}},
                {'bar': {}},
            ]
        }
        g = Grain(d)
        self.assertEqual(g.proxies(), ['one'])

    def test_roles(self):
        d = {
            'servers': [
                {'foo': {'roles': ['one']}},
                {'bar': {}},
            ]
        }
        g = Grain(d)
        self.assertEqual(g.roles(), ['one'])

    def test_by_app(self):
        d = {
            'servers': [
                {'foo': {'apps': ['one']}},
                {'bar': {}},
            ]
        }
        g = Grain(d)
        r = list(g.by_app())
        self.assertEqual(len(r), 1)
        self.assertEqual(r[0]['app'], 'one')
        self.assertEqual(r[0]['servers'][0].name, 'foo')

    def test_by_proxy(self):
        d = {
            'servers': [
                {'foo': {'proxy': ['one']}},
                {'bar': {}},
            ]
        }
        g = Grain(d)
        r = list(g.by_proxy())
        self.assertEqual(len(r), 1)
        self.assertEqual(r[0]['proxy'], 'one')
        self.assertEqual(r[0]['servers'][0].name, 'foo')

    def test_by_role(self):
        d = {
            'servers': [
                {'foo': {'roles': ['one']}},
                {'bar': {}},
            ]
        }
        g = Grain(d)
        r = list(g.by_role())
        self.assertEqual(len(r), 1)
        self.assertEqual(r[0]['role'], 'one')
        self.assertEqual(r[0]['servers'][0].name, 'foo')


class ServerTest(unittest.TestCase):
    def test_keys(self):
        s = Server('foo', dict(a="b"))
        self.assertEqual(list(s.keys()), ["a"])

    def test_apps(self):
        s = Server('foo', dict(apps=['one', 'two', 'three']))
        self.assertEqual(s.apps(), ['one', 'two', 'three'])

    def test_proxy(self):
        s = Server('foo', dict(proxy=['one', 'two', 'three']))
        self.assertEqual(s.proxy(), ['one', 'two', 'three'])

    def test_roles(self):
        s = Server('foo', dict(roles=['one', 'two', 'three']))
        self.assertEqual(s.roles(), ['one', 'two', 'three'])
