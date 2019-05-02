from grids.Grid import Grid
import pygame


class ExpandableGrid(Grid):
    def __init__(self, parent, position, cell_size, columns=25, rows=25):
        super().__init__(parent, columns, rows, cell_size, position, scale=1)
        self.scores = [0, 0]

    def updateInput(self):
        pass

    def updateScore(self):
        pass

    def convert(self, position):
        pass

    def add(self, value, position):
        pass

    def render(self):
        pass

