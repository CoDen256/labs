from grids.Grid import Grid
import pygame.gfxdraw as pydraw
import pygame


class StaticGrid(Grid):
    def __init__(self, parent, position, cell_size, columns=3, rows=3):
        super().__init__(parent, columns, rows, cell_size, position, scale=1)
        self.scores = [0, 0]

        self.touched = False
        self.last_pos = None

    def update_input(self, event):
        # Only handling the touching: grid is not resizable
        # Handles each event in loop
        if event.type == pygame.MOUSEBUTTONDOWN:
            if event.button == 1:
                self.last_pos = pygame.mouse.get_pos()
        if event.type == pygame.MOUSEBUTTONUP:
            if event.button == 1:
                self.touched = True

    def update(self):
        # Nothing to update
        pass

    def _check_winner(self, grid):
        # rows
        for x in range(0, 3):
            row = set([grid[x][0], grid[x][1], grid[x][2]])
            if len(row) == 1 and grid[x][0] != 0:
                return grid[x][0]

        # columns
        for x in range(0, 3):
            column = set([grid[0][x], grid[1][x], grid[2][x]])
            if len(column) == 1 and grid[0][x] != 0:
                return grid[0][x]

        # diagonals
        diag1 = set([grid[0][0], grid[1][1], grid[2][2]])
        diag2 = set([grid[0][2], grid[1][1], grid[2][0]])
        if (len(diag1) == 1 or len(diag2) == 1) and grid[1][1] != 0:
            return grid[1][1]

        return 0

    def render(self):
        # Parent surface blits grid surface at the position
        self.parent.blit(self.surface, (self.x, self.y))

        self.render_grid()
        self.render_cells()

        self.touched = False

    @property
    def is_just_pressed(self):
        # If no touched returns False
        # Otherwise returns the position of touching
        if not self.touched:
            return False

        return self.last_pos
