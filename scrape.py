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
        with open(f"C:\\dev\\k\\datasets\\group\\{key.replace(' ', '')}.csv", "w", newline='') as f:
            writer = csv.writer(f, delimiter=",")
            for row in value:
                print(row)
                writer.writerow([row])
            

group()
# ipv4 = scrape("ip/analysis", "68.47.128.161", parseIpReverseDns)
# print(ipv4)
# ipv4Pulses = scrapeAPI(IndicatorTypes.IPv4, "68.47.128.161", "general", parseIpPulses)
# print(ipv4Pulses)