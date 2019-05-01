from grids.Grid import Grid
import pygame.gfxdraw as pydraw
import pygame


class StaticGrid(Grid):
    def __init__(self, parent, position, cell_size, columns=3, rows=3):
        super().__init__(parent, columns, rows, cell_size, position)
        self.scores = [0, 0]

    def updateScore(self):
        pass

    def convert(self, position):
        """ Converts absolute position to array position and returns False if unsuccessful"""
        x, y = position
        if not (self.x < x < self.x + self.width) or \
           not (self.y < y < self.y + self.height):

            return False
    
        return (self.grid_to_arr(self.surf_to_grid(position)))

    def updateInput(self):
        """ No update is needed: grid is not resizable """
        pass

    def add(self, value, position):
        # Adds particular move to a position(relative to Array), returns True if successful
        if not self.cells[position[0]][position[1]]:
            self.cells[position[0]][position[1]] = value
            return True
        else:
            return False

    def render(self):
        self.parent.blit(self.surface, (self.x, self.y))
        #fill the surface
        self.renderGrid()
        self.renderCells()

    def renderGrid(self):
        # Draws a grid: vertical and horizontal lines
        for i in range(0, self.width+1, self.cell_size):
            pydraw.vline(self.surface, i, 0, self.height, (50, 50, 50))

        for j in range(0, self.height+1, self.cell_size):
            pydraw.hline(self.surface, 0, self.width, j, (50, 50, 50))

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

        pydraw.line(self.parent,
                    x, y,
                    x + self.cell_size, y + self.cell_size, (255, 50, 50))

        pydraw.line(self.parent,
                    x + self.cell_size, y,
                    x, y + self.cell_size, (255, 50, 50))

    def renderCircle(self, position):
        # renders a circle at the position relative to grid
        x, y = self.grid_to_surf(self.arr_to_grid(position))

        pydraw.aacircle(self.parent,
                        x + self.cell_size//2, y + self.cell_size//2,
                        self.cell_size//2, (50, 50, 255))

        pydraw.aacircle(self.parent,
                        x + self.cell_size//2, y + self.cell_size//2,
                        self.cell_size//4, (50, 50, 255))

    def grid_to_surf(self, pos):
        # Converts Grid coordinates to self.parent coordinates
        return (self.x + pos[0], self.y + pos[1])

    def surf_to_grid(self, pos):
        # Converts self.parent coordinates to Grid coordinates
        return (pos[0] - self.x, pos[1] - self.y)
    
    def arr_to_grid(self, pos):
        # Converts Array coordinates to Grid coordinates
        return (pos[0] * self.cell_size, pos[1] * self.cell_size)

    def grid_to_arr(self, pos):
        # Converts Grid coordinates to Array coordinates
        return (pos[0] // self.cell_size, pos[1] // self.cell_size)

    def highlight(self, position):
        """ Highlights certain position(relative to parent) """
        x,y = position

        if not (self.x < x < self.x + self.width) or \
           not (self.y < y < self.y + self.height):
            return
        x,y = self.surf_to_grid((x,y))
        x = (x // self.cell_size) * self.cell_size
        y = (y // self.cell_size) * self.cell_size
        x,y = self.grid_to_surf((x,y))

        pygame.draw.rect(self.parent, (180, 180, 180), [x, y, self.cell_size, self.cell_size])