from typing import List


class IOC:
    def __init__(self, name, weight):
        self.name = name
        self.weight = weight

    def __eq__(self, other):
        if isinstance(other, IOC):
            return self.name == other.name
        return False

    def __hash__(self):
        return hash(self.name)


class WeightedIOCEntry:
    def __init__(self, name, memory, pulses):
        self.name = name
        self.memory = memory
        self.pulses = pulses

    def __eq__(self, other):
        if isinstance(other, WeightedIOCEntry):
            return self.name == other.name
        return False

    def __hash__(self):
        return hash(self.name)


class Subset:
    def __init__(self, iocs_list, price):
        self.price = price
        self.iocs_list = iocs_list[:]
