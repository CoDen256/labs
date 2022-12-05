import IndicatorTypes

from scrape_file import *


load_scrape_save("datasets/group/FileHash-SHA256.csv", "datasets/parsed/FileHash-SHA256_parsed.csv",
                 lambda h: scrape_file(h, IndicatorTypes.FILE_HASH_SHA256),
                 start=0,
                 count=300,
                 batch_size=50)