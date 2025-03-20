import requests
from OTXv2 import OTXv2, IndicatorTypes

host = "https://otx.alienvault.com/otxapi/indicators/"

otx = OTXv2("5acd5d1141d37b569138771e16cdca11142e388266d3073a92a27b81387aeb6d")


def scrape_details(indicator_type, indicator, section):
    return otx.get_indicator_details_by_section(indicator_type, indicator, section)

def scrape_full(indicator_type, indicator):
    return otx.get_indicator_details_full(indicator_type, indicator)


def scrape(endpoint, indicator):
    return requests.get(f"{host}/{endpoint}/{indicator}").json()

if __name__ == '__main__':
    print(scrape_details(IndicatorTypes.IPv6, "2001:1600:10:100::201", "general"))
