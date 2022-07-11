from mailjet_rest import Client
import os
api_key = 'c15432d03c157fb252545d1687e1ccd5'
api_secret = '37a26616f098de5942541bb8f17ae9bc'

def handle(req):
    """handle a request to the function
    Args:
        req (str): request body
    """
    send({
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
        "Subject": f"Greetings from Mailjet. {req}",
        "TextPart": "My first Mailjet email",
        "HTMLPart": "<h3>Dear passenger 1, welcome to <a href='https://www.mailjet.com/'>Mailjet</a>!</h3><br />May the delivery force be with you!",
        "CustomID": "AppGettingStartedTest"
        }
    ]
    })
    return req


def send(mail):
    print(f"Fake sent {mail}")
    return
    mailjet = Client(auth=(api_key, api_secret), version='v3.1')
    result = mailjet.send.create(data=mail)
    print (result.status_code)
    print (result.json())
