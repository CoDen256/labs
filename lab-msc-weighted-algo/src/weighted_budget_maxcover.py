from model import *


def subset_weight(covered, subset: Subset):
    weight_sum = 0
    for ioc in subset.iocs_list:
        if ioc not in covered:
            weight_sum += ioc.weight
    return weight_sum


def heaviest_subset(subsets, covered_iocs):
    all_weights = []
    for subset in subsets:
        all_weights.append((subset, subset_weight(covered_iocs, subset)))
    biggest_weight = max(all_weights, key=max_by_weight)
    if biggest_weight[1] == 0:
        return None
    return biggest_weight[0]


def max_by_weight(subset):
    return subset[1]


def weighted_budget(budget, subsets, covered_iocs, subset_sum, covered_subsets):
    while subset_sum < budget:
        heaviest_set = heaviest_subset(subsets, covered_iocs)
        if not heaviest_set:
            break
        else:
            if subset_sum + heaviest_set.price < budget:
                covered_iocs |= (set(heaviest_set.iocs_list))
                covered_subsets.append(heaviest_set)
                subset_sum += heaviest_set.price
            else:
                del subsets[subsets.index(heaviest_set)]
                weighted_budget(budget, subsets, covered_iocs, subset_sum, covered_subsets)
    return covered_subsets


def weighted_budget_maximum_cover(budget, subsets):
    covered_subsets = []  # subset_limit subsets
    covered_iocs = set()
    subset_sum = 0
    return weighted_budget(budget, subsets, covered_iocs, subset_sum, covered_subsets)
