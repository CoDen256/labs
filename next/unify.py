import csv
#url, class(benign, phishing), Domain Name, Registrar, Updated Date, Creation Date, Registry Expiry Date, Registrant Organization, Country

fields = ["url", "class","protocol", " Domain Name", "Registrar", "Updated Date", "Creation Date", "Registry Expiry Date", "Registrant Organization", "Country"]

def map_top500(row):
    return [
                            row[0], #url
                            "https",
                            "benign",
                            row[1], # Domain Name
                            row[2], #registrar
                            trun(row[5]), #updated_date
                            trun(row[6]), #creation_date
                            trun(row[7]), #expiration_date
                            row[13],#org
                            row[18],#country
                        ]

def trun(col): # truncate_to_date
    return col.split()[0]

def invalid_to_empty(col):
    if  "REDACTED" in col or col == "None":
        return ""
    return col

if __name__ == '__main__':
    with open("C:\\dev\\dataset-urls\\next\\parsed_whois_top500_2.csv", 'r', encoding="utf-8") as r:
        with open("top500_unified.csv", 'w', newline="", encoding="utf-8") as w:
            reader = csv.reader(r, delimiter=",")
            writer = csv.writer(w, delimiter=",")
            writer.writerow(fields)
            for row in reader:
                try:
                    new = map_top500(row)
                    print(new)
                    writer.writerow(list(map(lambda x: invalid_to_empty(x), new)))
                except Exception as e:
                    print(e)



## TOP 500
                            # row[0], #url
                            # "benign",
                            # row[1], # Domain Name
                            # row[2], #registrar
                            # row[3], #whois_server
                            # row[4], #referral
                            # row[5], #updated_date
                            # row[6], #creation_date
                            # row[7], #expiration_date
                            # row[8], #name+servers
                            # row[9], #status
                            # row[10],#emails
                            # row[11],#dnssec
                            # row[12],#name
                            # row[13],#org
                            # row[14],#address
                            # row[15],#city
                            # row[16],#state
                            # row[17],#postal
                            # row[18],#country