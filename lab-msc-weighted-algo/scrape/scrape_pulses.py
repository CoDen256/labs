import IndicatorTypes

from utils import *
from api import *


def scrape_file(indicator, type):
    details = scrape_details(type, indicator, "general")
    return [indicator, details["pulse_info"]["count"]], None


if __name__ == '__main__':
    load_scrape_save("datasets/group/domain.csv", "datasets/parsed/domain_parsed.csv",
                     lambda h: scrape_file(h, IndicatorTypes.DOMAIN),
                     start=0,
                     count=500,
                     batch_size=50)

    load_scrape_save("datasets/group/email.csv", "datasets/parsed/email_parsed.csv",
                     lambda h: scrape_file(h, IndicatorTypes.EMAIL),
                     start=0,
                     count=64,
                     batch_size=10)

