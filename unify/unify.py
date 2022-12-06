from model import *
from typing import List


def unify_hostnames_domains_ips(hostnames: List[Hostname], domains: List[Domain], ips: List[IP]):
    total = set()
    primary_domains = list(map(lambda h: h.domain, hostnames))
    primary_hostnames = list(map(lambda h: h.value, hostnames))

    result_ip = [ip for ip in ips if ip.hostname not in primary_hostnames]
    result_domains = [domain for domain in domains if domain.value not in primary_domains]

    total.update(result_ip)
    total.update(hostnames)
    total.update(result_domains)
    return total


def unify_md5_sha1_sha256(md5: List[FileHashMD5], sha1: List[FileHashSHA1], sha256: List[FileHashSHA256]):
    total = set()

    primary_ids = list(map(lambda sha256_hash: sha256_hash.id, sha256))

    result_md5 = list(filter(lambda file_hash: file_hash.id not in primary_ids, md5))
    secondary_ids = list(map(lambda md5_hash: md5_hash.id, result_md5))

    result_sha1 = list(filter(lambda file_hash: file_hash.id not in primary_ids and file_hash.id not in secondary_ids,
                              sha1))

    total.update(sha256)
    total.update(result_sha1)
    total.update(result_md5)
    return total


def convert_weighted(indicators):
    collect = []
    for indicator in indicators:
        ioc = WeightedIOC(indicator.value, 1 / len(indicator.value), indicator.pulses)
        collect.append(ioc)
    return collect
