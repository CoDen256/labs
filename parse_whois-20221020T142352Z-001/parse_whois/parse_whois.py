import sys
import argparse
import requests
from bs4 import BeautifulSoup
import csv

parser = argparse.ArgumentParser(description='Whois')
VALUES = ('domain name', 'domain', 'updated date', 'modified', 'creation date', 'created',
          'registrar registration expiration date', 'expires', 'paid-till', 'registry expiry date', 'registrar',
          'registrant', 'registrant name', 'person', 'registrant organization', 'organization', 'registrant country',
          'country')


def get_domain_info(domain):
    domain_without_tld, tld = domain.split(".", 1)
    request = requests.post('https://www.eurodns.com/actions/domainApi/request/getWhois',
                            data={'tld_name': f'{tld}', 'tld_unicodeName': f'{tld}',
                                  'domainWithoutTld': f'{domain_without_tld}', 'language': 'en', 'captchaResponse': ''})

    if request.status_code != 200:
        parser.exit(1, f'Response status code: {request.status_code}. Something wrong with api, '
                       f'please try again after ~10 seconds')

    response_html = BeautifulSoup(request.text, 'html.parser')
    if (pre := response_html.find('pre')) is None:
        parser.exit(1, 'No data available :^(')
    return pre.text.split('\\n')


def parse_domain_info(text):
    save = {}
    for raw in text:
        value = raw.split(': ')
        if value[0].lower() in VALUES:
            save[f'{value[0]}'] = value[1]
    return save


def save_to_csv(file, data):
    with open(file, 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(data.keys())
        writer.writerow(data.values())


def main():
    parser.add_argument('domain', help='Domain name to parse, something like wikipedia.org')
    parser.add_argument('save_filename', help='File for save whois results')
    args = parser.parse_args(sys.argv[1:])
    domain_info = get_domain_info(args.domain)
    save_data = parse_domain_info(domain_info)
    save_to_csv(args.save_filename, save_data)


if __name__ == '__main__':
    main()
