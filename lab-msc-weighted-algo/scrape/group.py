import csv
import glob


def group():
    groups = {}
    for filepath in glob.iglob('C:\\dev\\k\\datasets\\*.csv'):
        with open(filepath) as f:
            reader = csv.reader(f, delimiter=",")
            for row in reader:
                print(row)
                if row[0] not in groups:
                    groups[row[0]] = []
                groups[row[0]].append(row[1])
    print(groups)
    for (key, value) in groups.items():
        print("writing", key)
        with open(f"C:\\dev\\k\\datasets\\group\\{key.replace(' ', '')}_2.csv", "w", newline='') as f:
            writer = csv.writer(f, delimiter=",")
            for row in value:
                print(row)
                writer.writerow([row])
