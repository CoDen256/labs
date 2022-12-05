import csv

base = "C:\\dev\\k"


def scrape_save_batch(source_indicators, target, scrape):
    collect = []
    for indicator in source_indicators:
        print(f"\nScraping {indicator:<60} ... ", end='')
        try:
            res, error = scrape(indicator)
            if error is not None:
                print(f"IGNORE: {error}", end='')
                continue
            collect.append(res)
            print(f"SUCCESS", end='')
        except Exception as e:
            print(f"ERROR: {e} ", end='')
    print()
    save(target, collect)
    print(f"Written {len(collect)} entries")
    return len(collect)

def chunks(lst, n):
    """Yield successive n-sized chunks from lst."""
    for i in range(0, len(lst), n):
        yield lst[i:i + n]

def load_scrape_save(source, target, scrape, start=0, count=-1, batch_size=-1):
    indicators = load(source)
    end = len(indicators) if count < 0 else start + count
    batch_size = len(indicators) if batch_size < 0 else batch_size
    total = indicators[start:end]
    processed = 0
    succeded = 0
    for indicator_batch in chunks(total, batch_size):
        print(f"\n---------------\nStarting new batch [{processed} - {processed+batch_size}]", end='')
        success_batch = scrape_save_batch(indicator_batch, target, scrape)
        succeded += success_batch
        processed += len(indicator_batch)
        print(f"BATCH: \t\t{success_batch}/{batch_size}")
        print(f"TOTAL: \t\t{succeded}/{processed}")

def load(file):
    with open(f"{base}\\{file}", "r", newline="") as f:
        reader = csv.reader(f, delimiter=",")
        collect = []
        for row in reader:
            collect.append(row[0])
        return collect


def save(file, list):
    with open(f"{base}\\{file}", "a", newline="") as f:
        writer = csv.writer(f, delimiter=",")
        for row in list:
            writer.writerow(row)
