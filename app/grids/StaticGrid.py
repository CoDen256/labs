from grids.Grid import Grid
import pygame.gfxdraw as pydraw
import pygame


class StaticGrid(Grid):
    def __init__(self, parent, position, cell_size, columns=3, rows=3):
        super().__init__(parent, columns, rows, cell_size, position, scale=1)
        self.scores = [0, 0]

    def render(self):
        # Parent surface blits grid surface at the position
        self.parent.blit(self.surface, (self.x, self.y))

        self.renderGrid()
        self.renderCells()

    def updateScore(self):
        pass

    def updateInput(self, event):
        """ No update is needed: grid is not resizable """
        pass

    def update(self):
        """ No update is needed """
        pass
