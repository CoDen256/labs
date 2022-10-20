import csv
import sys
from selenium import webdriver
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.chrome.options import Options
from fake_useragent import UserAgent

"""
Example of parsed data:

<dt>Network</dt>
<dd>
<pre>85.117.234.0/23 (AS51659 "<i>ASBAXET, RU</i>")</pre>
</dd>

<dt>WHOIS</dt>
<dd>
   Domain Name: ROKOIOKN.CC
   Registry Domain ID: 156923254_DOMAIN_CC-VRSN
   Registrar WHOIS Server: whois.discount-domain.com
   Registrar URL: http://gmo.jp
   Updated Date: 2021-04-18T14:40:58Z
   Creation Date: 2021-04-17T03:07:35Z
   Registry Expiry Date: 2022-04-17T03:07:35Z
   Registrar: GMO Internet, Inc. d/b/a Onamae.com
   Registrar IANA ID: 49
   Registrar Abuse Contact Email: abuse@gmo.jp
   Registrar Abuse Contact Phone: +81.337709199
   Domain Status: ok https://icann.org/epp#ok
   Name Server: NS1.ALIDNS.COM
   Name Server: NS2.ALIDNS.COM
   DNSSEC: unsigned
   URL of the ICANN Whois Inaccuracy Complaint Form: https://www.icann.org/wicf/
>>> Last update of WHOIS database: 2021-04-18T17:49:57Z <<<

For more information on Whois status codes, please visit https://icann.org/epp 
etc...
</dd>
"""

opts = Options() #setup options for webdriver
opts.add_argument("window-size=1400,600")

def save_to_file(file_name, scraped_data):
    with open(file_name, mode='w') as output_file:
        writer = csv.writer(output_file, delimiter=':', quotechar='"', quoting=csv.QUOTE_MINIMAL)
        for row in scraped_data:
            writer.writerow(row, scraped_data[row]) #save data to .scv


def parse(phish_id):
    ua = UserAgent()
    user_agent = ua.random #generete random User-Agent
    print(user_agent)
    opts.add_argument(f"user-agent={user_agent}") # pass it as a parameter in the Selenium webdriver
    driver = webdriver.Chrome(chrome_options=opts, executable_path=ChromeDriverManager().install()) #Selenium webdriver autoinstall
    driver.get(f'http://phishtank.org/phish_detail.php?phish_id={phish_id}&frame=details') #connecting
    driver.implicitly_wait(10000) #wait for page to load
    print(driver.page_source) #return or print data we need


if __name__ == "__main__":
    scraped_data = parse(sys.argv[1])
    save_to_file(sys.argv[2], scraped_data)

