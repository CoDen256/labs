from grids.Grid import Grid
import pygame.gfxdraw as pydraw


class StaticGrid(Grid):
    def __init__(self, w=3, h=3):
        self.cells = [[0 for i in range(w)] for j in range(h)]
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

    def render(self, surface):
        surface_w, surface_h = surface.get_size()
        pydraw.line(surface, 0, 0, surface_w, surface_h, (50, 50, 50))
