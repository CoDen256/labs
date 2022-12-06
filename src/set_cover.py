from model import *
from typing import Set, List


def set_cover(universe: Set[IOC], subsets: List[Subset]): #List[Set[str]]
    elements: Set[IOC] = set(e for subset in subsets for e in subset.iocs_list)
    # Check the subsets cover the universe
    if elements != universe:
        return None
    covered: Set[IOC] = set()
    cover: List[Subset] = []
    # Greedily add the subsets with the most uncovered points
    while covered != elements:
        subset = max(subsets, key=lambda subset: len(set(subset.iocs_list) - covered))
        cover.append(subset)
        covered |= set(subset.iocs_list)
    return cover
