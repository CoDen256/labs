import requests
import random
import os

base = "http://gateway.openfaas:8080/function/"

def handle(req):
    functions_env = os.getenv("functions", "")
    functions = list(filter(lambda x: x, map(lambda x: x.strip(), functions_env.split(","))))

    if (not functions):
        return "No functions specified"

    random_fn = random.choice(functions)
    return "random fn:\n"+requests.post(base+random_fn, data=req).content.decode()
