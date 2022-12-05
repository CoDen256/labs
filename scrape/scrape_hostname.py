import IndicatorTypes

from utils import *
from api import *


def scrape_hostname(hostname):
    general = scrape_details(IndicatorTypes.HOSTNAME, hostname, "general")
    passive_dns = scrape_details(IndicatorTypes.HOSTNAME, hostname, "passive_dns")
    domain_name = general["domain"]
    ips = [d["address"] for d in passive_dns["passive_dns"]]
    pulses = general["pulse_info"]["count"]
    return [hostname, domain_name, ";".join(ips), pulses], None


load_scrape_save("datasets/group/hostname.csv", "datasets/parsed/hostname_parsed.csv", scrape_hostname,
                 start=0,
                 count=1,
                 batch_size=1)
