class IOC:
  def __init__(self, name, weight, pulses=0):
    self.name = name
    self.weight = weight
    self.pulses = pulses
  def __eq__(self, other):
    return self.name == other.name
  def __hash__(self):
    return hash(self.name)

class Subset:
    def __init__(self, iocs_list, price):
      self.price = price     # computed sum of bytes of all (non unique) entries
      self.iocs_list = iocs_list[:]