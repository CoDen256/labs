import IndicatorTypes

from utils import *
from api import *

def scrape_ip(ip, type):
    dns = scrape("ip/analysis", ip)["facts"]["reverse_dns"]
    pulses = scrape_details(type, ip, "general")["pulse_info"]["count"]
    return [ip, dns, pulses], None


load_scrape_save("datasets/group/IPv4.csv", "datasets/parsed/IPv4_parsed.csv", lambda ip: scrape_ip(ip, IndicatorTypes.IPv4),
                 start=0,
                 count=50,
                 batch_size=10)

load_scrape_save("datasets/group/IPv6.csv", "datasets/parsed/IPv6_parsed.csv",
                 lambda ip: scrape_ip(ip, IndicatorTypes.IPv6),
                 start=0,
                 count=50,
                 batch_size=10)