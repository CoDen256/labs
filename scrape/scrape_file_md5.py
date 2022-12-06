import IndicatorTypes

from scrape_file import *

load_scrape_save("datasets/group/FileHash-MD5.csv", "datasets/parsed/FileHash-MD5_parsed.csv",
                 lambda h: scrape_file(h, IndicatorTypes.FILE_HASH_MD5),
                 start=0,
                 count=500,
                 batch_size=50)