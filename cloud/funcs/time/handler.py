import datetime
def handle(req):
    return datetime.datetime.now().strftime("%d.%m.%y %H:%M:%S.%f")
