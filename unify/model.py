class Domain:
    def __init__(self, domain):
        self.domain = domain


class Email:
    def __init__(self, email):
        self.email = email


class FileHashMD5:
    def __init__(self, hash, id, pulses):
        self.hash = hash
        self.id = id


class FileHashSHA1:
    def __init__(self, hash, id, pulses):
        self.hash = hash
        self.id = id


class FileHashSHA256:
    def __init__(self, hash, id, pulses):
        self.hash = hash
        self.id = id

class Hostname:
    def __init__(self, hostname, domain, ips, pulses):
        self.hostname = hostname
        self.domain = domain
        self.ips = ips[:]
        self.pulses = pulses

class IP:
    def __init__(self, ip, hostname, pulses):
        self.hostname = hostname
        self.ip = ip
        self.pulses = pulses