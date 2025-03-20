import random
from typing import List
from utils import *
from model import *
from load_unify_save import compile_ioc

source = "datasets/unique/total.csv"
target_path = "datasets/source/"


def parse_weighted_ioc(row):
    return WeightedIOC(row[0], float(row[1]), int(row[2]))


def load_universe(limit=-1):
    universe = load(source, parse_weighted_ioc)
    default = len(universe)
    return universe[:limit if limit > 0 else default]


def generate_subsets(universe: List[WeightedIOC],
                     subsets_count=1,
                     subset_limit=10):
    subsets: List[List[WeightedIOC]] = []

    for i in range(subsets_count):
        indicators: List[WeightedIOC] = []
        for j in range(subset_limit):
            indicators.append(random.choice(universe))
        subsets.append(indicators)

    return subsets


def trim_randomly(subsets, range):
    trimmed = []
    for subset in subsets:
        trimmed.append(subset[:-random.randrange(0, range)])
    return trimmed


def duplicate_randomly(subsets, max_duplicates, chance):
    duplicated = subsets[:]
    for index, subset in enumerate(duplicated):
        for indicator in subset:
            if random.randint(0, chance) == 0:
                target = random.randint(1, max_duplicates-1)
                for i in range(1, target):
                    duplicated[(i + index) % max_duplicates].append(indicator)
    return duplicated # set() - randomize order


def save_subsets(subsets):
    for index, subset in enumerate(subsets):
        print(f"Saving subset {index}")
        save(f"{target_path}/subset_{index}.csv", subset, compile_ioc)


def main():
    universe = load_universe()
    primary = generate_subsets(universe, 1, 20)[0]  # 200
    first_sub = primary[:5]  # 0-50
    second_sub = primary[5:]  # 50-200

    secondary = generate_subsets(universe, 7, 10)
    print(f"Generated secondary {len(secondary)} {[len(s) for s in secondary]}")

    randomized = duplicate_randomly(secondary, 6, 10)  # 100-150
    print(f"Randomized {len(secondary)} {[len(s) for s in secondary]}")

    total = [primary, first_sub, second_sub] + randomized
    print(f"Generated {len(total)} {[len(s) for s in total]}")
    save_subsets(total)


if __name__ == '__main__':
    main()
