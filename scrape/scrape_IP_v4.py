import IndicatorTypes

from scrape_IP import *

load_scrape_save("datasets/group/IPv4.csv", "datasets/parsed/IPv4_parsed.csv",
                 lambda ip: scrape_ip(ip, IndicatorTypes.IPv4),
                 start=300,
                 count=100,
                 batch_size=50)
