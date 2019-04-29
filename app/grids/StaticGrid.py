from grids.Grid import Grid


class StaticGrid(Grid):
    def __init__(self, w=20, h=10):
        self.cells = [[-1 for i in range(w)] for j in range(h)]
        self.width = w
        self.height = h

        self.scores = [0, 0]

        self.scale = 1

    def updateInput(self):
        pass

    def updateScore(self):
        pass

    def convert(self, position):
        pass

    def add(self, value, position):
        pass
