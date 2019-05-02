from grids.Grid import Grid
import pygame.gfxdraw as pydraw
import pygame


class StaticGrid(Grid):
    def __init__(self, parent, position, cell_size, columns=3, rows=3):
        super().__init__(parent, columns, rows, cell_size, position, scale=0.5)
        self.scores = [0, 0]

    def updateScore(self):
        pass

    def updateInput(self):
        """ No update is needed: grid is not resizable """
        pass
