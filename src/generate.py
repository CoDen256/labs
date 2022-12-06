import csv
import random
from typing import List

from model import *
import os.path

source = "../datasets/unique/total.csv"


def parse_ioc(row):
    return IOC(row[0], float(row[1]), row[2])


def load(file, parser):
    print(f"Loading {file}")
    path = f"{os.path.dirname(__file__)}\\{file}"
    with open(path, "r", newline="") as f:
        reader = csv.reader(f, delimiter=",")
        collect = []
        for row in reader:
            collect.append(parser(row))
        print(f"Loaded {len(collect)} entries")
        return collect


def load_universe(limit=-1):
    universe = load(source, parse_ioc)
    default = len(universe)
    return universe[:limit if limit > 0 else default]


def generate_universe_and_subsets(subsets_count=1,
                                  subset_limit=10,
                                  universe_limit=-1):
    subsets = []
    universe: List[IOC] = load_universe(universe_limit)
    duplicate_previous_chance = 2
    for i in range(subsets_count):
        indicators: List[IOC] = []
        price = 0
        for j in range(subset_limit):
            indicator = random.choice(universe)
            indicators.append(indicator)
            price += indicator.weight
        subsets.append(Subset(indicators, price))

    return universe, subsets

def main():
    universe, subsets = generate_universe_and_subsets(3, 15, -1)
    print(subsets)

if __name__ == '__main__':
    main()
