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

def map_phishtank(row):
    return [
                            row[0], #url
                            ["benign", "phishing"][row[1]=="yes"], #verified
                            row[0].split(":")[0],
                            row[4], #domain name
                            row[8], #registrar
                            trunT(row[5]), #updated
                            trunT(row[6]), #creation_date
                            trunT(row[7]), #expiration_date
                            row[10],#registrant org
                            row[11],#country
                        ]

def trun(col): # truncate_to_date
    return col.split()[0]

def trunT(col): # truncate_to_date
    return col.split("T")[0]

def invalid_to_empty(col):
    if  "REDACTED" in col or col == "None":
        return ""
    return col

if __name__ == '__main__':
    with open("C:\\dev\\dataset-urls\\next\\parsed_phishtank.csv", 'r', encoding="utf-8") as r:
        with open("phishtank_unified.csv", 'w', newline="", encoding="utf-8") as w:
            reader = csv.reader(r, delimiter=",")
            writer = csv.writer(w, delimiter=",")
            writer.writerow(fields)
            for row in reader:
                try:
                    new = map_phishtank(row)
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

## phishtank
                            # row[0], #url
                            # row[1], #verified
                            # row[2], #id
                            # row[3], #network
                            # row[4], #domain name
                            # row[5], #updated
                            # row[6], #creation_date
                            # row[7], #expiration_date
                            # row[8], #registrar
                            # row[9], #registrant name
                            # row[10],#registrant org
                            # row[11],#country