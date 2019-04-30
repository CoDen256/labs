from grids.Grid import Grid


class ExpandableGrid(Grid):
    def __init__(self, w=100, h=100):
        self.cells = [[0 for i in range(w)] for j in range(h)]
        self.width = w
        self.height = h

        self.scores = [0, 0]

        self.scale = 4

    def updateInput(self):
        pass

    def updateScore(self):
        pass

    def convert(self, position):
        pass

    def add(self, value, position):
        pass

    def render(self, surface):
        pass
