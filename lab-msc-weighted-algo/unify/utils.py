import csv

base = "C:\\dev\\k"


def load(file, supplier):
    print(f"Loading {file}")
    with open(f"{base}\\{file}", "r", newline="") as f:
        reader = csv.reader(f, delimiter=",")
        collect = []
        for row in reader:
            collect.append(supplier(row))
        print(f"Loaded {len(collect)} entries")
        return collect


def save(file, list, to_row):
    with open(f"{base}\\{file}", "a", newline="") as f:
        writer = csv.writer(f, delimiter=",")
        for row in list:
            writer.writerow(to_row(row))

