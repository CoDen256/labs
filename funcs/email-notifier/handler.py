from mailjet_rest import Client
import requests
api_key = 'c15432d03c157fb252545d1687e1ccd5'
api_secret = '37a26616f098de5942541bb8f17ae9bc'

pwd_read_function_endpoint = "http://gateway.openfaas:8080/function/pwd-reader"

def handle(req):
    r = requests.get(pwd_read_function_endpoint)
    send(mail(req))
    return r.content


def mail(content):
    return {
    'Messages': [
        {
        "From": {
            "Email": "den.blackshov@gmail.com",
            "Name": "Denys"
        },
        "To": [
            {
            "Email": "den.blackshov@gmail.com",
            "Name": "Denys"
            }
        ],
        "Subject": f"Greetings from Mailjet. {content}",
        "TextPart": "My first Mailjet email",
        "HTMLPart": "<h3>Dear passenger 1, welcome to <a href='https://www.mailjet.com/'>Mailjet</a>!</h3><br />May the delivery force be with you!",
        "CustomID": "AppGettingStartedTest"
        }
    ]
    }

def send(mail):
    print(f"Fake sent {mail}")
    return
    mailjet = Client(auth=(api_key, api_secret), version='v3.1')
    result = mailjet.send.create(data=mail)
    print (result.status_code)
    print (result.json())
