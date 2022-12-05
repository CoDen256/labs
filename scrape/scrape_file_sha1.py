import IndicatorTypes

from scrape_file import *

load_scrape_save("datasets/group/FileHash-SHA1.csv", "datasets/parsed/FileHash-SHA1_parsed.csv",
                 lambda h: scrape_file(h, IndicatorTypes.FILE_HASH_SHA1),
                 start=0,
                 count=300,
                 batch_size=50)
