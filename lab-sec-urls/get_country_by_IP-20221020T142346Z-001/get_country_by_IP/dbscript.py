import geoip2.database
ip = "google.com"
with geoip2.database.Reader('C:\\dev\dataset-urls\\get_country_by_IP-20221020T142346Z-001\\get_country_by_IP\\GeoLite2-Country.mmdb') as reader:
	response = reader.country(ip)
	print(response.country.iso_code)
	print(response.country.name)
	print(response.traits.network)
	
	 
	
	
