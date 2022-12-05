from utils import *
from api import *


def parseIpPulses(page):
    return page["pulse_info"]["count"]


def parseIpReverseDns(page):
    return page["facts"]["reverse_dns"]


def scrape_ip(ip):
    dns = scrape("ip/analysis", ip, parseIpReverseDns)
    if dns is None: return None, "no dns"
    pulses = scrape_details(IndicatorTypes.IPv4, ip, "general", parseIpPulses)
    return [ip, dns, pulses], None


load_scrape_save("datasets/group/IPv4.csv", "datasets/parsed/IP_parsed.csv", scrape_ip,
                 start=0,
                 count=10,
                 batch_size=3)
