from utils import  *
from api import *

def parseIpPulses(page):
    return page["pulse_info"]["count"]

def parse_hostname(page):
    return page


def parseIpReverseDns(page):
    return page["facts"]["reverse_dns"]


def scrape_hostname(hostname):
    dns = scrape("ip/analysis", ip, parseIpReverseDns)
    if dns is None: return None
    pulses = scrape_details(IndicatorTypes.IPv4, ip, "general", parseIpPulses)



# load_scrape_save_hostname()

