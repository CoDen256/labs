from bs4 import BeautifulSoup
import csv
import sys
import requests

fields = ["url", "verified", "id", 'Network','Domain Name', 'Updated Date', 'Creation Date', 'Registry Expiry Date', 'Registrar', 'Registrant Name',
              'Registrant Organization', 'Registrant Country']

def parse_phishtank(link_id):
    print(f"requesting {link_id}")
    data = {}
    r = requests.get(f'http://phishtank.org/technical_details.php?phish_id={link_id}')
    response = BeautifulSoup(r.text, 'html.parser')
    text = response.find_all('pre')
    for field in fields:
        data[field] = None
    data['Network'] = text[0].text.split(' ')[0]
    for value in text[1].text.split('\r\n'):
        v = value.split(': ')
        if v[0].strip() in fields:
            data[f'{v[0]}'] = v[1]
    print(data)
    print("\n"*2)
    return data


if __name__ == '__main__':
    with open("C:\\dev\\dataset-urls\\aggregated\\phishtank copy.csv", 'r') as r:
        with open("parsed_phishtank_6.csv", 'w', newline="") as f:
            reader = csv.reader(r, delimiter=",")
            writer = csv.writer(f)
            writer.writerow(fields)
            for row in reader:
                try:
                    data = parse_phishtank(row[0])
                    # writer.writerow(data.keys())
                    #url, verified, Network,Domain Name,Updated Date,Creation Date,Registry Expiry Date,Registrar,Registrant Name,Registrant Organization,Registrant Country
                    data['url'] = row[1]
                    data['verified'] = row[2]
                    data["id"] = row[0]
                    writer.writerow(data.values())
                except Exception as e:
                    print(e)
                    pass
