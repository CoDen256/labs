import requests, os
from OTXv2 import OTXv2, IndicatorTypes
import csv
import glob
host = "https://otx.alienvault.com/otxapi/indicators/"
targed = "parsed"

otx = OTXv2("5acd5d1141d37b569138771e16cdca11142e388266d3073a92a27b81387aeb6d")
#print(otx.get_indicator_details_full(IndicatorTypes.IPv6, "93.95.227.106"))

def scrapeAPI(indicator_type, indicator, section, parse_function):
    return parse_function(otx.get_indicator_details_by_section(indicator_type, indicator, section))

def scrape(endpoint, indicator, parse_function):
    return parse_function(requests.get(f"{host}/{endpoint}/{indicator}").json())

def parseIpPulses(page):
    return page["pulse_info"]["count"]

def parse_hostname(page):
    return page

def parseIpReverseDns(page):
    return page["facts"]["reverse_dns"]

def loadIndicators(file, index, filter = lambda x: True):
    with open(file, "r", newline="") as f:
        reader = csv.reader(f, delimiter=",")
        collect = []
        for row in reader:
            if (filter(row)):
                collect.append(row[index])
        return collect
def save(file, list):
    with open(file, "w", newline="") as f:
        writer = csv.writer(f, delimiter=",")
        for row in list:
            print(row)
            writer.writerow([row])

def fixIps4():
    list0 = loadIndicators("C:\\dev\\k\\datasets\\IP.csv", 1, lambda x: x[0] == "IPv4")
    list1 = loadIndicators("C:\\dev\\k\\datasets\\ipV4_2.csv", 1, lambda x: x[0] == "IPv4")
    list2 = loadIndicators("C:\\dev\\k\\datasets\\ipv4.csv", 1, lambda x: x[0] == "IPv4")
    list3 = loadIndicators("C:\\dev\\k\\datasets\\ipV6.csv", 1, lambda x: x[0] == "IPv4")
    list4 = loadIndicators("C:\\dev\\k\\datasets\\ipV6_2.csv", 1, lambda x: x[0] == "IPv4")
    total = set()
    for l in [list0, list1, list2, list3, list4]:
        total |= set(l)
    print(total)
    save("C:\\dev\\k\\datasets\\_IPV4.csv", list(total))

def fixIps6():
    list0 = loadIndicators("C:\\dev\\k\\datasets\\IP.csv", 1, lambda x: x[0] == "IPv6")
    list1 = loadIndicators("C:\\dev\\k\\datasets\\ipV4_2.csv", 1, lambda x: x[0] == "IPv6")
    list2 = loadIndicators("C:\\dev\\k\\datasets\\ipv4.csv", 1, lambda x: x[0] == "IPv6")
    list3 = loadIndicators("C:\\dev\\k\\datasets\\ipV6.csv", 1, lambda x: x[0] == "IPv6")
    list4 = loadIndicators("C:\\dev\\k\\datasets\\ipV6_2.csv", 1, lambda x: x[0] == "IPv6")
    total = set()
    for l in [list0, list1, list2, list3, list4]:
        total |= set(l)
    print(total)
    save("C:\\dev\\k\\datasets\\_IP6.csv", list(total))

def fixDomains():
    list0 = loadIndicators("C:\\dev\\k\\datasets\\IP.csv", 1, lambda x: x[0] == "IPv6")
    list1 = loadIndicators("C:\\dev\\k\\datasets\\ipV4_2.csv", 1, lambda x: x[0] == "IPv6")
    list2 = loadIndicators("C:\\dev\\k\\datasets\\ipv4.csv", 1, lambda x: x[0] == "IPv6")
    list3 = loadIndicators("C:\\dev\\k\\datasets\\ipV6.csv", 1, lambda x: x[0] == "IPv6")
    list4 = loadIndicators("C:\\dev\\k\\datasets\\ipV6_2.csv", 1, lambda x: x[0] == "IPv6")
    total = set()
    for l in [list0, list1, list2, list3, list4]:
        total |= set(l)
    print(total)
    save("C:\\dev\\k\\datasets\\IP6.csv", list(total))


def group():
    groups = {}
    for filepath in glob.iglob('C:\\dev\\k\\datasets\\*.csv'):
        with open(filepath) as f:
            reader = csv.reader(f, delimiter=",")
            for row in reader:
                print(row)
                if (row[0] not in groups):
                    groups[row[0]] = []
                groups[row[0]].append(row[1])
    print(groups)
    for (key, value) in groups.items():
        print("writing", key)
        with open(f"C:\\dev\\k\\datasets\\group\\{key.replace(' ', '')}_2.csv", "w", newline='') as f:
            writer = csv.writer(f, delimiter=",")
            for row in value:
                print(row)
                writer.writerow([row])
            

def parseIp4():
    ipv4s = loadIndicators("C:\\dev\\k\\datasets\\group\\IPv4.csv", 0)
    collect = []
    for ip in ipv4s[500:700]:
        print(f"scraping {ip}")
        try:
            dns = scrape("ip/analysis", ip, parseIpReverseDns)
            if dns is None: continue
            pulses = scrapeAPI(IndicatorTypes.IPv4, ip, "general", parseIpPulses)
            collect.append([ip, dns, pulses])
        except Exception as e:
            print(f"Exception {e} for {ip}")
    with open(f"C:\\dev\\k\\datasets\\parsed\\IP_parsed.csv", "w", newline='') as f:
        writer = csv.writer(f, delimiter=",")
        for row in collect:
            writer.writerow(row)
    print(f"Written {len(collect)} entries")

def parseHostname():
    ipv4s = loadIndicators("C:\\dev\\k\\datasets\\group\\hostname.csv", 0)
    collect = []
    for ip in ipv4s:
        print(f"scraping {ip}")
        try:
            dns = scrape("ip/analysis", ip, parseIpReverseDns)
            if dns is None: continue
            pulses = scrapeAPI(IndicatorTypes.IPv4, ip, "general", parseIpPulses)
            collect.append([ip, dns, pulses])
        except Exception as e:
            print(f"Exception {e} for {ip}")
    with open(f"C:\\dev\\k\\datasets\\parsed\\IP_parsed.csv", "w", newline='') as f:
        writer = csv.writer(f, delimiter=",")
        for row in collect:
            writer.writerow(row)
    print(f"Written {len(collect)} entries")
parseIp4()
#parseHostname()
# ipv4 = scrape("ip/analysis", "68.47.128.161", parseIpReverseDns)
# print(ipv4)
#ipv4Pulses = scrapeAPI(IndicatorTypes.HOSTNAME, "di.upa.penega.com", "general", parse_hostname)
#print(otx.get_indicator_details_full(IndicatorTypes.HOSTNAME, "device-0ec82908-d2df-491b-bca8-2bc0b603db56.remotewd.com"))
#print(ipv4Pulses)