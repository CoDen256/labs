from set_cover import set_cover
from weighted_max_cover import weighted_maximum_cover
from weighted_budget_maxcover import weighted_budget_maximum_cover
from load import *
from save import *


def prompt_initial():
    print("Welcome!")


def prompt_subset_limit():
    limit = int(input("Enter the number of subsets [3-10]"))
    return limit


def prompt_algo(algo_choice):
    for (key, algo) in algo_choice:
        name, _ = algo
        print(f"{key}.{name}")
    choice = int(input("Enter the algorithm that you want to select:"))
    return algo_choice[choice]


def invoke_set_cover(subsets: List[Subset]):
    return set_cover()


def invoke_weighted_maximum_cover(subsets: List[Subset]):
    pass


def invoke_weighted_budgeted_maximum_cover(subsets: List[Subset]):
    pass


algo_mapping = [

]


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

    save("result.txt", result)

if __name__ == '__main__':
    main()
