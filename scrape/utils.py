import csv

base = "C:\\dev\\k"


def load(file):
    with open(f"{base}\\{file}", "r", newline="") as f:
        reader = csv.reader(f, delimiter=",")
        collect = []
        for row in reader:
            collect.append(row[0])
        return collect


def save(file, list):
    with open(f"{base}\\{file}", "w", newline="") as f:
        writer = csv.writer(f, delimiter=",")
        for row in list:
            writer.writerow(row)
