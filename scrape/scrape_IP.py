from utils import *
from api import *

def scrape_ip(ip):
    dns = scrape("ip/analysis", ip)["facts"]["reverse_dns"]
    if dns is None: return None, "no dns"
    pulses = scrape_details(IndicatorTypes.IPv4, ip, "general")["pulse_info"]["count"]
    return [ip, dns, pulses], None


load_scrape_save("datasets/group/IPv4.csv", "datasets/parsed/IP_parsed.csv", scrape_ip,
                 start=0,
                 count=10,
                 batch_size=10)
