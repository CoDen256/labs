import geoip2.database
ip = "93.78.208.67"
with geoip2.database.Reader('GeoLite2-Country.mmdb') as reader:
	response = reader.country(ip)
	print(response.country.iso_code)
	print(response.country.name)
	print(response.traits.network)
	
	 
	
	
