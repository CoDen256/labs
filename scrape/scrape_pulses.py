import IndicatorTypes

from utils import *
from api import *


def scrape_file(indicator, type):
    details = scrape_details(type, hash, "general")
    return [indicator, details["pulse_info"]["count"]], None


if __name__ == '__main__':
    load_scrape_save("datasets/group/domain.csv", "datasets/parsed/FileHash-MD5_parsed.csv",
                     lambda h: scrape_file(h, IndicatorTypes.FILE_HASH_MD5),
                     start=0,
                     count=300,
                     batch_size=50)

    load_scrape_save("datasets/group/email.csv", "datasets/parsed/FileHash-SHA1_parsed.csv",
                     lambda h: scrape_file(h, IndicatorTypes.FILE_HASH_SHA1),
                     start=0,
                     count=300,
                     batch_size=50)

