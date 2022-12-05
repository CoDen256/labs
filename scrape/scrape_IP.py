from utils import *
from api import *


def parseIpPulses(page):
    return page["pulse_info"]["count"]


def parseIpReverseDns(page):
    return page["facts"]["reverse_dns"]


def parseIp4():
    ipv4s = load("datasets/group/IPv4.csv")
    collect = []
    for ip in ipv4s[:5]:
        print(f"scraping {ip}")
        try:
            dns = scrape("ip/analysis", ip, parseIpReverseDns)
            if dns is None: continue
            pulses = scrape_details(IndicatorTypes.IPv4, ip, "general", parseIpPulses)
            collect.append([ip, dns, pulses])
        except Exception as e:
            print(f"Exception {e} for {ip}")
    save("datasets/parsed/IP_parsed.csv", collect)
    print(f"Written {len(collect)} entries")

parseIp4()