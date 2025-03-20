from set_cover import set_cover
from weighted_max_cover import weighted_maximum_cover
from weighted_budget_maxcover import weighted_budget_maximum_cover
from load import load_subsets
from save import save
from typing import List, Set
from model import Subset, IOC, WeightedIOCEntry


def prompt_initial():
    print("Welcome!")


def prompt_subset_limit():
    limit = int(input("Enter the number of subsets [3-10] to work with: "))
    return limit


def prompt_limit_covered_subsets():
    limit = int(input("Enter the limit of covered subsets: "))
    return limit


def prompt_subset_budget():
    limit = int(input("Enter the budget in bytes"))
    return limit


def prompt_algo(algo_choice):
    for (key, algo) in algo_choice.items():
        name, _ = algo
        print(f"{key}.{name}")
    choice = int(input("Enter the algorithm that you want to select:"))
    return algo_choice[choice]


def prompt_weight_type(weight_choice):
    for (key, weight) in weight_choice.items():
        name, _ = weight
        print(f"{key}.{name}")
    choice = int(input("Enter the type of weight based on which IOCs will be processed:"))
    return weight_choice[choice]


def flatten(subsets: List[Subset]) -> Set[IOC]:
    return set([e for s in subsets for e in s.iocs_list])


def convert_to_IOCs(subsets: List[Subset], weight_extractor) -> List[Subset]:
    total: List[Subset] = []
    for s in subsets:
        indicators = []
        for indicator in s.iocs_list:
            ioc = IOC(indicator.name, weight_extractor(indicator))
            indicators.append(ioc)
        total.append(Subset(indicators, s.price))
    return total


def invoke_set_cover(subsets: List[Subset]):
    normalized = convert_to_IOCs(subsets, lambda i: i.weight)
    universe = flatten(normalized)
    return set_cover(universe, normalized)


def invoke_weighted_maximum_cover(subsets: List[Subset]):
    limit = prompt_limit_covered_subsets()

    name, weight_extractor = prompt_weight_type({
        0: ("Pulses", lambda indicator:  indicator.pulses),
        1: ("Memory", lambda indicator:  indicator.memory)
    })

    normalized = convert_to_IOCs(subsets, weight_extractor=weight_extractor)
    return weighted_maximum_cover(limit, normalized)


def invoke_weighted_budgeted_maximum_cover(subsets: List[Subset]):
    budget = prompt_subset_budget()

    name, weight_extractor = prompt_weight_type({
        0: ("Pulses", lambda indicator:  indicator.pulses),
        1: ("Memory", lambda indicator:  indicator.memory)
    })

    normalized = convert_to_IOCs(subsets, weight_extractor=weight_extractor)
    return weighted_budget_maximum_cover(budget, normalized)


def main():
    algos = {
        1: ("Set Cover", invoke_set_cover),
        2: ("Weighted Cover", invoke_weighted_maximum_cover),
        3: ("Weighted Budgeted Maximum Cover", invoke_weighted_budgeted_maximum_cover)
    }
    prompt_initial()
    limit = prompt_subset_limit()
    subsets = load_subsets(limit)

    algo_result = prompt_algo(algos)
    name, algo = algo_result
    result: List[Subset] = algo(subsets)

    print(f"Percent of covered IOCs: {len(flatten(result))/len(flatten(subsets))*100}% ")
    save("result.txt", result)
    print(f"Written to result.txt: {len(result)} sets")


if __name__ == '__main__':
    main()
