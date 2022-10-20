import requests
ip = "62.16.0.0"
locationInfo = requests.get('https://geolocation-db.com/json/'+ip+'&position=true').json()

print ('Country: ' + locationInfo['country_name'])
print ('IP: ' + locationInfo['IPv4'])
print (locationInfo)