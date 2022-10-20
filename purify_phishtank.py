import csv


with open("verified_online.csv", newline='') as f:
    with open("phishtank.csv", 'w', newline='') as w:
        reader = csv.reader(f, delimiter=",")
        writer = csv.writer(w, delimiter=",")
        for row in reader:
            writer.writerow([row[1], row[4], "https" if row[1].startswith("https") else "http"])      