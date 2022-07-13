count = 0
def handle(req):
    global count
    count += 1
    return str(count), 200
