import pymongo
import json

def handle(password):
    client = pymongo.MongoClient("mongodb+srv://coden256:UTcFVnSbGyEKjXDO@cloud-cluster.0mx4b.mongodb.net/?retryWrites=true&w=majority&tls=true&tlsAllowInvalidCertificates=true")
    result = client.db.passwords.insert_one({"value": password})
    return f"Successful: {result.acknowledged}"