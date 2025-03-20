import glob

from model import *
import csv
import os.path


def parse_weighted_IOC(row):
    return WeightedIOCEntry(row[0], float(row[1]), int(row[2]))


def load(file, supplier):
    print(f"Loading {file}")
    with open(file, "r", newline="") as f:
        reader = csv.reader(f, delimiter=",")
        collect = []
        for row in reader:
            collect.append(supplier(row))
        print(f"Loaded {len(collect)} entries")
        return collect


def get_file_size(file):
    return os.stat(file).st_size


def load_subsets(limit):
    files = f"{os.path.dirname(__file__)}\\..\\datasets\\source\\*.csv"
    subsets = []
    for index, filepath in enumerate(glob.iglob(files)):
        if index >= limit: break
        print(f"Found {filepath}")
        subset = load(filepath, parse_weighted_IOC)
        size = get_file_size(filepath)
        print(f"Loaded {len(subset)} iocs")
        print(f"Size: {size}")
        print()
        subsets.append(Subset(subset, size))
    print("-" * 10)
    for index, subset in enumerate(subsets):
        print(f"Subset {index}: {len(subset.iocs_list)} indicators, {subset.price} bytes ")
    return subsets
