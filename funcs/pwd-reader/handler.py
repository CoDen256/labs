import pymongo
import json

def handle(req):
    client = pymongo.MongoClient("mongodb+srv://coden256:UTcFVnSbGyEKjXDO@cloud-cluster.0mx4b.mongodb.net/?retryWrites=true&w=majority&tls=true&tlsAllowInvalidCertificates=true")
    return json.dumps([{"password" : i['value']} for i in client.db.passwords.find()])

print(handle(None))