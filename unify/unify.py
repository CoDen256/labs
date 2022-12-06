

def unify_hostnames_domains_ips(hostnames, domains, ips):
    total = []
    total += list(set(ips))
    total += list(set(hostnames))
    total += list(set(domains))
    return total


def unify_md5_sha1_sha256(md5, sha1, sha256):
    pass