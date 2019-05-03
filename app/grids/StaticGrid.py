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

    def update_score(self):
        pass

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
