import requests
from OTXv2 import OTXv2, IndicatorTypes

host = "https://otx.alienvault.com/otxapi/indicators/"

otx = OTXv2("5acd5d1141d37b569138771e16cdca11142e388266d3073a92a27b81387aeb6d")


def scrape_details(indicator_type, indicator, section, parse_function):
    return parse_function(otx.get_indicator_details_by_section(indicator_type, indicator, section))


def scrape(endpoint, indicator, parse_function):
    return parse_function(requests.get(f"{host}/{endpoint}/{indicator}").json())

