import os.path
from typing import List
from model import *

base = f"{os.path.dirname(__file__)}\\.."


def to_str(index, subset: Subset):
    result = f"Subset {index}: \n"
    for indicator in subset.iocs_list:
        result += f"{indicator.name}\n"
    result += "\n"
    return result


def save(file, subsets: List[Subset]):
    with open(f"{base}\\{file}", "w", newline="") as f:
        for index, subset in enumerate(subsets):
            f.write(to_str(index, subset))

