from mailjet_rest import Client
api_key = 'c15432d03c157fb252545d1687e1ccd5'
api_secret = '37a26616f098de5942541bb8f17ae9bc'

def handle(request):
    if (not request or len(request) < 2):
        return "Mail is to small"
    return send(mail(request))


def mail(content):
    return {
    'Messages': [
        {
        "From": {
            "Email": "den.blackshov@gmail.com",
            "Name": "OpenFaas Server"
        },
        "To": [
            {
            "Email": "den.blackshov@gmail.com",
            "Name": "Denys"
            }
        ],
        "Subject": f"Function Call Result",
        "HTMLPart": content,
        "CustomID": "AppGettingStartedTest"
        }
    ]
    }

def send(mail):
    mailjet = Client(auth=(api_key, api_secret), version='v3.1')
    result = mailjet.send.create(data=mail)
    return f"{result.status_code} :\n{result.json()}"