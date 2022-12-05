import IndicatorTypes

from scrape_IP import *


load_scrape_save("datasets/group/IPv6.csv", "datasets/parsed/IPv6_parsed.csv",
                 lambda ip: scrape_ip(ip, IndicatorTypes.IPv6),
                 start=0,
                 count=300,
                 batch_size=50)