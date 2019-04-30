from grids.Grid import Grid
import pygame.gfxdraw as pydraw


class StaticGrid(Grid):
    def __init__(self, surface, x, y, cell_size, columns=3, rows=3):
        self.cells = [[0 for i in range(columns)] for j in range(rows)]
        self.columns = columns
        self.rows = rows

        self.surface = surface

        self.position = self.x, self.y = x, y
        self.cell_size = cell_size
        self.height = cell_size * rows
        self.width = cell_size * columns

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
        self.renderGrid()
        self.renderCells()

    def renderGrid(self):
        # Draws a grid: vertical and horizontal lines
        for i in range(0, self.width+1, self.cell_size):
            pydraw.vline(self.surface, self.x+i, self.y, self.y+self.height, (50, 50, 50))

        for j in range(0, self.height+1, self.cell_size):
            pydraw.hline(self.surface, self.x, self.x+self.width, self.y+j, (50, 50, 50))

    def renderCells(self):
        for row in range(len(self.cells)):
            for col in range(len(self.cells[row])):
                if self.cells[row][col] == 1:
                    self.renderCross((row, col))
                elif self.cells[row][col] == -1:
                    self.renderCircle((row, col))

    def renderCross(self, position):
        # renders a cross at the position relative to grid
        x, y = self.grid_to_surf(self.arr_to_grid(position))

        pydraw.line(self.surface,
                    x, y,
                    x + self.cell_size, y + self.cell_size, (255, 50, 50))

        pydraw.line(self.surface,
                    x + self.cell_size, y,
                    x, y + self.cell_size, (255, 50, 50))

    def renderCircle(self, position):
        # renders a circle at the position relative to grid
        x, y = self.grid_to_surf(self.arr_to_grid(position))

        pydraw.aacircle(self.surface,
                        x + self.cell_size//2, y + self.cell_size//2,
                        self.cell_size//2, (50, 50, 255))

        pydraw.aacircle(self.surface,
                        x + self.cell_size//2, y + self.cell_size//2,
                        self.cell_size//4, (50, 50, 255))

    def grid_to_surf(self, pos):
        # Converts Grid coordinates to self.surface coordinates
        return (self.x + pos[0], self.y + pos[1])

    def surf_to_grid(self, pos):
        # Converts self.surface coordinates to Grid coordinates
        return (pos[0] - self.x, pos[1] - self.y)
    
    def arr_to_grid(self, pos):
        # Converts Array coordinates to Grid coordinates
        return (pos[0] * self.cell_size, pos[1] * self.cell_size)

    def grid_to_arr(self, pos):
        # Converts Grid coordinates to Array coordinates
        return (pos[0] // self.cell_size, pos[1] // self.cell_size)
