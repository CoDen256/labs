from model import *

def subset_max(subsets):
    return len(s - covered)

def set_cover(universe, subsets):
    elements = set(e for s in subsets for e in s)
    # Check the subsets cover the universe
    if elements != universe:
        return None
    covered = set()
    cover = []
    # Greedily add the subsets with the most uncovered points
    while covered != elements:
        subset = max(subsets, key=subset_max(subsets))
        cover.append(subset)
        covered |= subset
    return cover