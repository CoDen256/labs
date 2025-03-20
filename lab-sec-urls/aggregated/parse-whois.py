import whois 
import csv

fields = ['url', 'domain_name', 'registrar', 'whois_server', 'referral_url', 'updated_date', 'creation_date', 'expiration_date', 
'name_servers', 'status', 'emails', 'dnssec', 'name', 'org', 'address', 'city', 'state', 'registrant_postal_code', 'country']

def parse_whois(domain):
    print(f"parsing {domain}")
    res = whois.whois(domain)
    return dict(res)

def flatten(data, key):
    if key in data and isinstance(data[key], list):
        data[key] = data[key][0]    

with open("C:\\dev\\dataset-urls\\aggregated\\phishtank.csv", 'r') as r:
    with open("parsed_whois_phish_2.csv", 'w', newline="", encoding='utf-8') as f:
        reader = csv.reader(r, delimiter=",")
        writer = csv.writer(f)
        writer.writerow(fields)
        for row in reader:
            try:
                data = parse_whois(row[1])
                # writer.writerow(data.keys())
                #Network,Domain Name,Updated Date,Creation Date,Registry Expiry Date,Registrar,Registrant Name,Registrant Organization,Registrant Country
                data_res = data
                flatten(data, "domain_name")
                flatten(data,"updated_date")
                flatten(data,"creation_date")
                flatten(data,"expiration_date")
                flatten(data,"name_servers")
                flatten(data,"status")
                flatten(data,"emails")
                data_res['url'] = row[0]
                res_row = []
                for f in fields:
                    if (f not in data_res):
                        res_row.append(None)
                        continue
                    try: 
                        res_row.append(str(data_res[f]))
                    except Exception as e:
                        res_row.append(data_res[f])
                writer.writerow(res_row)
            except Exception as e:
                print(e)
                pass