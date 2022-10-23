import requests
def getIpInfo(data):
    locationInfo=requests.get('https://geolocation-db.com/json/'+data+'&position=true').json()
    print ('Country: '+locationInfo['country_name'])
    print ('IP: '+locationInfo['IPv4'])
    print (locationInfo)
    
ip = "google.com"
info = getIpInfo(ip)