import requests
from bs4 import BeautifulSoup
import sys
 
 
class StatusCodeError(RuntimeError):
    pass
 
 
def get_top_500():
    r = requests.get("http://www.delong.com/ipv6_alexa500.html")
    if r.status_code != 200:
        raise StatusCodeError('Maybe some problems with site. Try again later.')
 
    soup = BeautifulSoup(r.text, 'html.parser')
    fields = soup.find_all("th")
 
    sites = []
    for i in range(len(fields)):
        if i > 6 and i % 2 == 1:
            sites.append(fields[i].text)
    return sites
 
 
def save_txt(file, data):
    with open(file, 'w') as f:
        for site in data:
            f.write(f'{site}\n')
 
 
if __name__ == '__main__':
    try:
        save_filename = sys.argv[1]
    except IndexError:
        print("You forget to give save_file value.\nTry 'python <script_name.py> <save_file>'", file=sys.stderr)
        sys.exit(1)
    save_data = get_top_500()
    save_txt(save_filename, save_data)