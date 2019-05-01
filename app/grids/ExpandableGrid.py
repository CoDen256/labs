from grids.Grid import Grid
import pygame

class ExpandableGrid(Grid):
    def __init__(self, parent, position, cell_size, columns=25, rows=25):
        super().__init__(parent, columns, rows, cell_size, position)

        self.cell_size = cell_size

        self.width = cell_size * columns
        self.height = cell_size * rows

        self.grid_surface = pygame.Surface((self.width, self.height))
        self.x, self.y = 100, 100

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

    def render(self):
        pass

    def highlight(self, position):
        pass
