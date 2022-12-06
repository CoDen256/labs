from model import *
from itertools import groupby

from unify import *
from utils import *


def parse_domain(row):
    return Domain(row[0], int(row[1]))


def parse_file_md5(row):
    return FileHashMD5(row[0], row[1], int(row[2]))


def parse_file_sha1(row):
    return FileHashSHA1(row[0], row[1], int(row[2]))


def parse_file_sha256(row):
    return FileHashSHA256(row[0], row[1], int(row[2]))


def parse_email(row):
    return Email(row[0], 4)


def parse_ip(row):
    return IP(row[0], row[1], int(row[2]))


def parse_hostname(row):
    return Hostname(row[0], row[1], int(row[3]))


def compile_ioc(ioc: WeightedIOC):
    return [ioc.value, ioc.weight, int(ioc.pulses)]


def load_files():
    print("----")

    md5 = load("datasets/parsed/FileHash-MD5_parsed.csv", parse_file_md5)
    sha1 = load("datasets/parsed/FileHash-SHA1_parsed.csv", parse_file_sha1)
    sha256 = load("datasets/parsed/FileHash-SHA256_parsed.csv", parse_file_sha256)

    print(f"Unifying md5({len(md5)}) sha1({len(sha1)}) sha256({len(sha256)}) ...")
    unified = unify_md5_sha1_sha256(md5, sha1, sha256)
    print(f"Unified total (md5, sha1,sha256): {len(unified)} ")
    log_distribution(unified)

    return unified


def load_hostnames():
    print("----")

    ipv4 = load("datasets/parsed/IPv4_parsed.csv", parse_ip)
    ipv6 = load("datasets/parsed/IPv6_parsed.csv", parse_ip)
    hostname = load("datasets/parsed/hostname_parsed.csv", parse_hostname)
    domain = load("datasets/parsed/domain_parsed.csv", parse_domain)

    print(f"Unifying hostnames({len(hostname)}) domains({len(domain)}) ipv4({len(ipv4)}) ipv6({len(ipv6)})...")

    unified = unify_hostnames_domains_ips(hostname, domain, ipv4 + ipv6)

    print(f"Unified total (md5, sha1,sha256): {len(unified)} ")
    log_distribution(unified)
    return unified

def load_emails():
    print("----")
    emails = set(load("datasets/group/email.csv", parse_email))
    print(f"Loaded {len(emails)} emails")
    return emails

def convert_and_save(list):
    print("---")


    print(f"Converting and computing weight of {len(list)} elements...")
    w: List[WeightedIOC] = convert_weighted(list)
    pulse_sum = sum([ioc.pulses for ioc in w])
    pulse_total = len(w)
    print(f"Total len: \t{pulse_total}")
    print(f"Total sum: \t{pulse_sum}")
    print(f"Total avg: \t{float(pulse_sum)/float(pulse_total)}")

    print(f"Successfully converted")
    save("datasets/unique/total.csv", w, compile_ioc)
    print(f"Written {len(w)}")


def log_distribution(iocs):
    sorted_list = sorted(iocs, key=lambda i: str(type(i)))

    group = [(k, len([i for i in g])) for k, g in groupby(sorted_list, key=lambda x: str(type(x)))]
    for (k, l) in group:
        print(f"{k}: {l}")

if __name__ == '__main__':
    convert_and_save(load_files() | load_hostnames() | load_emails())
