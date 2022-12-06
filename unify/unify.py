from model import *
from typing import List


def unify_hostnames_domains_ips(hostnames: List[Hostname], domains: List[Domain], ips: List[IP]):
    total = []
    primary_domains = list(map(lambda h: h.domain, hostnames))
    primary_hostnames = list(map(lambda h: h.hostname, hostnames))

    result_ip = [ip for ip in ips if ip.hostname not in primary_hostnames]
    result_domains = [domain for domain in domains if domain.domain not in primary_domains]

    total += list(set(result_ip))
    total += list(set(hostnames))
    total += list(set(result_domains))
    return total


def unify_md5_sha1_sha256(md5, sha1, sha256):
    pass
