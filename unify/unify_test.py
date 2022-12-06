from unittest import TestCase
from unify import *
from model import *

class Test(TestCase):
    def test_model_equality(self):
        self.assertEqual(Hostname("h", "d", 0), Hostname("h", "d", 0))
        self.assertEqual(IP("i", "h", 0), IP("i", "h", 0))
        self.assertEqual(FileHashMD5("h", 1, 0), FileHashMD5("h", 1, 0))
        self.assertEqual(FileHashSHA1("h", 1, 0), FileHashSHA1("h", 1, 0))
        self.assertEqual(FileHashSHA256("h", 1, 0), FileHashSHA256("h", 1, 0))
        self.assertEqual(Email("e", 0), Email("e",  0))
        self.assertEqual(Domain("d", 0), Domain("d", 0))


    def test_unify_ips(self):
        ips = [IP("i2", "h", 0), IP("i1", "h", 1)]
        result = unify_hostnames_domains_ips([], [], ips)
        self.assertCountEqual(ips, result)

    def test_unify_ips_same(self):
        ips = [IP("i1", "h", 1), IP("i1", "h", 1)]
        result = unify_hostnames_domains_ips([], [], ips)
        self.assertCountEqual(ips[:1], result)

    def test_unify_hostnames(self):
        hostnames = [Hostname("h0", "d0", 0), Hostname("h1", "d1", 1)]
        domains = []
        ips = []
        result = unify_hostnames_domains_ips(hostnames, domains, ips)
        self.assertCountEqual(hostnames, result)


    def test_unify_hostnames_same(self):
        hostnames = [Hostname("h0", "d0", 0), Hostname("h0", "d0", 0)]
        domains = []
        ips = []
        result = unify_hostnames_domains_ips(hostnames, domains, ips)
        self.assertCountEqual(hostnames[:1], result)

    def test_unify_domains(self):
        hostnames = []
        domains = [Domain("d1", 10), Domain("d2", 20)]
        ips = []
        result = unify_hostnames_domains_ips(hostnames, domains, ips)
        self.assertCountEqual(domains, result)


    def test_unify_domains_same(self):
        hostnames = []
        domains = [Domain("d1", 10), Domain("d1", 10)]
        ips = []
        result = unify_hostnames_domains_ips(hostnames, domains, ips)
        self.assertCountEqual(domains[:1], result)

    def test_unify_domains_hostname_ips_unique(self):
        hostnames = [Hostname("h0", "d0", 0), Hostname("h1", "d0", 0)]
        domains = [Domain("d1", 10), Domain("d2", 10)]
        ips = [IP("i1", "h", 1), IP("i2", "h", 1)]
        result = unify_hostnames_domains_ips(hostnames, domains, ips)
        self.assertCountEqual(hostnames+domains+ips, result)

    def test_unify_hostnames_domains(self):
        hostnames = [Hostname("h0", "d0", 0), Hostname("h1", "d1", 1)]
        domains = [Domain("d1", 10), Domain("d2", 20)]
        ips = []
        result = unify_hostnames_domains_ips(hostnames, domains, ips)

    def test_unify_hostnames_ips(self):
        hostnames = [Hostname("h0", "d0", 0), Hostname("h1", "d1", 1)]
        domains = [Domain("d1", 10), Domain("d2", 20)]
        ips = []
        result = unify_hostnames_domains_ips(hostnames, domains, ips)

    def test_unify_hostnames_domains_ips(self):
        hostnames = [Hostname("h0", "d0", 0), Hostname("h1", "d1", 1)]
        domains = [Domain("d1", 10), Domain("d2", 20)]
        ips = []
        result = unify_hostnames_domains_ips(hostnames, domains, ips)

    def test_unify_domains_ips(self):
        hostnames = [Hostname("h0", "d0", 0), Hostname("h1", "d1", 1)]
        domains = [Domain("d1", 10), Domain("d2", 20)]
        ips = []
        result = unify_hostnames_domains_ips(hostnames, domains, ips)

    def test_files(self):
        md5 = []
        sha1 = []
        sha256 = []
        result = unify_md5_sha1_sha256(md5, sha1, sha256)
