from bs4 import BeautifulSoup
import csv
import sys
import requests


def parse_phishtank(link_id, savefile):
    data = {}
    r = requests.get(f'http://phishtank.org/technical_details.php?phish_id={link_id}')
    response = BeautifulSoup(r.text, 'html.parser')
    text = response.find_all('pre')
    data['Network'] = text[0].text.split(' ')[0]
    for value in text[1].text.split('\r\n'):
        v = value.split(': ')
        if v[0].strip() in fields:
            data[f'{v[0]}'] = v[1]
    with open(savefile, 'w') as f:
        writer = csv.writer(f)
        writer.writerow(data.keys())
        writer.writerow(data.values())


if __name__ == '__main__':
    fields = ['Domain Name', 'Updated Date', 'Creation Date', 'Registry Expiry Date', 'Registrar', 'Registrant Name',
              'Registrant Organization', 'Registrant Country']
    link = sys.argv[1]
    savefile = sys.argv[2]
    hashmap = parse_phishtank(link, savefile)
