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
        self.assertEqual(Email("e", 0), Email("e", 0))
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

        self.assertCountEqual(hostnames + domains + ips, result)

    def test_unify_hostnames_domains(self):
        hostnames = [Hostname("unique_hostname", "unique_hostname", 0), Hostname("h", "d1", 0)]
        domains = [Domain("d1", 10), Domain("unique_domain", 20)]
        ips = []

        result = unify_hostnames_domains_ips(hostnames, domains, ips)

        self.assertCountEqual({
            Hostname("unique_hostname", "unique_hostname", 0),
            Hostname("h", "d1", 0),
            Domain("unique_domain", 20)
        }, result)

    def test_unify_hostnames_ips(self):
        hostnames = [Hostname("unique_hostname", "unique_hostname", 0), Hostname("h", "d1", 0)]
        domains = []
        ips = [IP("unique_ip", "unique_ip_hostname", 0), IP("ip", "h", 0)]

        result = unify_hostnames_domains_ips(hostnames, domains, ips)

        self.assertCountEqual({
            Hostname("unique_hostname", "unique_hostname", 0),
            Hostname("h", "d1", 0),
            IP("unique_ip", "unique_ip_hostname", 0)
        }, result)

    def test_unify_hostnames_domains_ips(self):
        hostnames = [Hostname("unique_hostname", "unique_hostname_domain", 0),
                     Hostname("unique_hostname", "unique_hostname_domain", 0),
                     Hostname("h0", "d0", 0),
                     Hostname("h1", "d1", 1),
                     Hostname("h2", "d2", 1)
                     ]
        domains = [
            Domain("d0", 10),
            Domain("d1", 10),
            Domain("unique_domain_0", 20),
            Domain("unique_domain_0", 20),
        ]

        ips = [
            IP("ip", "h0", 0),
            IP("ip", "h2", 0),
            IP("unique_ip_0", "unique_ip_hostname_0", 0),
            IP("unique_ip_0", "unique_ip_hostname_0", 0),
        ]

        result = unify_hostnames_domains_ips(hostnames, domains, ips)

        self.assertCountEqual({
            Hostname("unique_hostname", "unique_hostname_domain", 0),
            Hostname("h0", "d0", 0),
            Hostname("h1", "d1", 1),
            Hostname("h2", "d2", 1),
            Domain("unique_domain_0", 20),
            IP("unique_ip_0", "unique_ip_hostname_0", 0),
        }, result)

    def test_unify_domains_ips(self):
        hostnames = []
        domains = [Domain("0", 10), Domain("1", 20)]
        ips = [IP("0", "0", 0), IP("1", "1", 0), ]

        result = unify_hostnames_domains_ips(hostnames, domains, ips)

        self.assertCountEqual(
            domains + ips, result
        )

    def test_unify_md5(self):
        # ARRANGE
        md5 = [FileHashMD5("m", 0, 0), FileHashMD5("m", 1, 0)]
        sha1 = []
        sha256 = []

        # ACT
        result = unify_md5_sha1_sha256(md5, sha1, sha256)

        # ASSERT
        self.assertCountEqual(
            md5 + sha1 + sha256
            , result
        )

    def test_unify_md5_same(self):
        # ARRANGE
        md5 = [FileHashMD5("m", 0, 0), FileHashMD5("m", 0, 0)]
        sha1 = []
        sha256 = []

        # ACT
        result = unify_md5_sha1_sha256(md5, sha1, sha256)

        # ASSERT
        self.assertCountEqual(
            md5[:1] + sha1 + sha256
            , result
        )

    def test_unify_sha1(self):
        # ARRANGE
        md5 = []
        sha1 = [FileHashSHA1("1", 0, 0), FileHashSHA1("1", 1, 0)]
        sha256 = []

        # ACT
        result = unify_md5_sha1_sha256(md5, sha1, sha256)

        # ASSERT
        self.assertCountEqual(
            md5 + sha1 + sha256
            , result
        )

    def test_unify_sha1_same(self):
        # ARRANGE
        md5 = []
        sha1 = [FileHashSHA1("1", 0, 0), FileHashSHA1("1", 0, 0)]
        sha256 = []

        # ACT
        result = unify_md5_sha1_sha256(md5, sha1, sha256)

        # ASSERT
        self.assertCountEqual(
            md5 + sha1[:1] + sha256
            , result
        )

    def test_unify_sha256(self):
        # ARRANGE
        md5 = []
        sha1 = []
        sha256 = [FileHashSHA256("2", 0, 0), FileHashSHA256("2", 1, 0)]

        # ACT
        result = unify_md5_sha1_sha256(md5, sha1, sha256)

        # ASSERT
        self.assertCountEqual(
            md5 + sha1 + sha256
            , result
        )

    def test_unify_sha256_same(self):
        # ARRANGE
        md5 = []
        sha1 = []
        sha256 = [FileHashSHA256("2", 0, 0), FileHashSHA256("2", 0, 0)]

        # ACT
        result = unify_md5_sha1_sha256(md5, sha1, sha256)

        # ASSERT
        self.assertCountEqual(
            md5 + sha1 + sha256[:1]
            , result
        )

    def test_unify_md5_sha256(self):
        # ARRANGE
        md5 = [FileHashMD5("m", -1, 0), FileHashMD5("m", 1, 0)]
        sha1 = []
        sha256 = [FileHashSHA256("2", -2, 0), FileHashSHA256("2", 1, 0)]

        # ACT
        result = unify_md5_sha1_sha256(md5, sha1, sha256)

        # ASSERT
        self.assertCountEqual(
            {
                FileHashMD5("m", -1, 0),
                FileHashSHA256("2", -2, 0),
                FileHashSHA256("2", 1, 0),
            }
            , result
        )

    def test_unify_md5_sha1(self):
        # ARRANGE
        md5 = [FileHashMD5("m", 0, 0), FileHashMD5("m", -1, 0)]
        sha1 = [FileHashSHA1("1", 0, 0), FileHashSHA1("1", 1, 0)]
        sha256 = []

        # ACT
        result = unify_md5_sha1_sha256(md5, sha1, sha256)

        # ASSERT
        self.assertCountEqual(
            {
                FileHashSHA1("1", 1, 0),
                FileHashMD5("m", -1, 0),
                FileHashMD5("m", 0, 0),

            }
            , result
        )

    def test_unify_sha1_sha256(self):
        # ARRANGE
        md5 = []
        sha1 = [FileHashSHA1("1", 0, 0), FileHashSHA1("1", -1, 0)]
        sha256 = [FileHashSHA256("2", 0, 0), FileHashSHA256("2", 1, 0)]

        # ACT
        result = unify_md5_sha1_sha256(md5, sha1, sha256)

        # ASSERT
        self.assertCountEqual(
            {
                FileHashSHA256("2", 0, 0),
                FileHashSHA256("2", 1, 0),
                FileHashSHA1("1", -1, 0)
            }
            , result
        )

    def test_unify_md5_sha1_sha256(self):
        # ARRANGE
        sha256 = [
            FileHashSHA256("2", -1, 0),
            FileHashSHA256("2", -1, 0),

            FileHashSHA256("2", 0, 0), # sha256 + md5
            FileHashSHA256("2", 1, 0), # sha256 + md5 + sha1
            FileHashSHA256("2", 2, 0), # sha256 + sha1

        ]
        md5 = [
            FileHashMD5("m", -2, 0),
            FileHashMD5("m", -2, 0),

            FileHashMD5("m", 0, 0), # sha256 + md5
            FileHashMD5("m", 1, 0), # sha256 + md5 + sha1

            FileHashMD5("m", 3, 0)  # md5 + sha1
        ]
        sha1 = [
            FileHashSHA1("1", -3, 0),
            FileHashSHA1("1", -3, 0),

            FileHashSHA1("1", 1, 0),  # sha256 + md5 + sha1
            FileHashSHA1("1", 2, 0),  # sha256 + sha1

            FileHashSHA1("1", 3, 0)   # md5 + sha1
        ]

        # ACT
        result = unify_md5_sha1_sha256(md5, sha1, sha256)

        # ASSERT
        self.assertCountEqual(
            {
                FileHashSHA256("2", -1, 0),
                FileHashMD5("m", -2, 0),
                FileHashSHA1("1", -3, 0),

                FileHashSHA256("2", 0, 0),
                FileHashSHA256("2", 1, 0),
                FileHashSHA256("2", 2, 0),

                FileHashMD5("m", 3, 0)

            }
            , result
        )

    def test_unify_md5_sha256_sha1_unique(self):
        # ARRANGE
        md5 = [FileHashMD5("m", 0, 0), FileHashMD5("m", 1, 0)]
        sha1 = [FileHashSHA1("1", 2, 0), FileHashSHA1("1", 3, 0)]
        sha256 = [FileHashSHA256("2", 4, 0), FileHashSHA256("2", 5, 0)]

        # ACT
        result = unify_md5_sha1_sha256(md5, sha1, sha256)

        # ASSERT
        self.assertCountEqual(
            md5 + sha1 + sha256
            , result
        )
