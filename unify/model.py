from dataclasses import dataclass


@dataclass
class Domain:
    value: str
    pulses: int

    def __hash__(self):
        return hash(self.value)

    def __eq__(self, other):
        if isinstance(other, Domain):
            return self.value == other.value
        return False


@dataclass
class Email:
    value: str
    pulses: int

    def __hash__(self):
        return hash(self.value)

    def __eq__(self, other):
        if isinstance(other, Email):
            return self.value == other.value
        return False


@dataclass
class FileHashMD5:
    value: str
    id: int
    pulses: int

    def __hash__(self):
        return hash(self.id)

    def __eq__(self, other):
        if isinstance(other, FileHashMD5):
            return self.id == other.id
        return False


@dataclass
class FileHashSHA1:
    value: str
    id: int
    pulses: int

    def __hash__(self):
        return hash(self.id)

    def __eq__(self, other):
        if isinstance(other, FileHashSHA1):
            return self.id == other.id
        return False


@dataclass
class FileHashSHA256:
    value: str
    id: int
    pulses: int

    def __hash__(self):
        return hash(self.id)

    def __eq__(self, other):
        if isinstance(other, FileHashSHA256):
            return self.id == other.id
        return False


@dataclass
class Hostname:
    value: str
    domain: str
    pulses: int

    def __hash__(self):
        return hash(self.value)

    def __eq__(self, other):
        if isinstance(other, Hostname):
            return self.value == other.value
        return False


@dataclass
class IP:
    value: str
    hostname: str
    pulses: int

    def __hash__(self):
        return hash(self.value)

    def __eq__(self, other):
        if isinstance(other, IP):
            return self.value == other.value
        return False


@dataclass
class WeightedIOC:
    value: str
    weight: float
    pulses: int
