from model import *

def subset_weight(covered, subset):
    weight_sum = 0
    for ioc in subset: 
        if ioc not in covered:
            weight_sum += ioc.weight   
    return weight_sum

def heaviest_subset(subsets, covered): #[[ioc1, ioc3], [ioc2]]
    all_weights = []    # [([ioc1, ioc3], 10), ([ioc1, ioc3], 12)]
    for subset in subsets:
        all_weights.append((subset, subset_weight(covered, subset)))   
    biggest_weight = max(all_weights, key=max_by_weight) # ([ioc1, ioc3], 12)
    if biggest_weight[1] == 0:
       biggest_weight[0].clear()
    return biggest_weight[0]

def max_by_weight(subset):
    return subset[1]

def weighted_maximum_cover(subset_limit, subsets): # [[ioc], [ioc]]
    covered_subsets = [] #subset_limit subsets
    covered_iocs = set()
    for i in range (0, subset_limit):
        heaviest_set = heaviest_subset(subsets, covered_iocs)
        if heaviest_set == []:
            break
        covered_iocs |= (set(heaviest_set))
        covered_subsets.append(heaviest_set)
    return covered_subsets
