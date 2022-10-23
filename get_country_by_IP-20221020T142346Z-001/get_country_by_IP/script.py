import requests
import csv
import dns.resolver

fields = ["url", "country_name"]

def getIpInfo(data):
    locationInfo=requests.get('https://geolocation-db.com/json/'+data+'&position=true').json()
    print (locationInfo)
    return locationInfo

getIpInfo("x")

with open("C:\\dev\\dataset-urls\\next\\parsed_whois_top500_2.csv", "r", encoding="utf-8") as r:
    with open("geo_top500.csv", 'w', newline="", encoding="utf-8") as w:
            reader = csv.reader(r, delimiter=",")
            writer = csv.writer(w, delimiter=",")
            writer.writerow(fields)
            for row in reader:
                domain = row[0]
                print(f"\n\n{domain}")
                try:
                    result = getIpInfo(domain)
                    if ("country_name" not in result or not result["country_name"]):
                        continue
                    writer.writerow([row[0], result["country_name"]])
                except Exception as e:
                    print(e)
                