import csv


with open("majestic_million.csv", newline='') as f:
    with open("majestic.csv", 'w', newline='') as w:
        reader = csv.reader(f, delimiter=",")
        writer = csv.writer(w, delimiter=",")
        i = 10_000
        for row in reader:
            if (i <= 0 ): break
            i -= 1
            writer.writerow([row[0], row[1], row[4], "https" if row[1].startswith("https") else "http"])      