from grids.Grid import Grid
import pygame


class ExpandableGrid(Grid):
    def __init__(self, parent, position, cell_size, columns=25, rows=25):
        super().__init__(parent, columns, rows, cell_size, position, scale=0.5)
        self.scores = [0, 0]
        self.x, self.y = self.parent.x - self.x, self.parent.y - self.y

        self.last_pressed = None  # last pressed point

        self.first = None  # first pressed point on the drag
        self.full_delta = None  # full delta between last and first point

        self.dragged = False

    def update_input(self, event):
        # Handles each event in for loop
        if event.type == pygame.MOUSEBUTTONDOWN:
            if event.button == 4:  # mouse wheel rolled up
                self.resize(+0.05, event.pos)
            elif event.button == 5:  # mouse wheel rolled down
                self.resize(-0.05, event.pos)
            elif event.button == 1 and not self.dragged:
                self.dragged = True
                self.last_pressed = self.first = pygame.mouse.get_pos()

        if event.type == pygame.MOUSEBUTTONUP:
            if event.button == 1:

                self.dragged = False
                self.full_delta = self.first[0] - pygame.mouse.get_pos()[0], self.first[1] - pygame.mouse.get_pos()[1]

                self.last_pressed = None

    def update(self):
        if self.dragged:
            self.on_drag()

    def update_score(self):
        pass

    def render(self):
        self.parent.blit(self.surface, (self.x, self.y))

        self.render_grid()
        self.render_cells()

        self.full_delta = None
        # updating full_delta

    def on_drag(self):
        delta = pygame.mouse.get_pos()[0] - self.last_pressed[0], pygame.mouse.get_pos()[1] - self.last_pressed[1]
        self.last_pressed = pygame.mouse.get_pos()

        self.x = self.x + delta[0]
        self.y = self.y + delta[1]

        self.check_boundaries()

    def resize(self, delta_scale, scale_point):
        if self.scale + delta_scale < 0.15 \
           or self.scale + delta_scale > 0.5:
            return

        point_x, point_y = self.surf_to_grid(scale_point)

        ratio_x = point_x / self.cell_size
        ratio_y = point_y / self.cell_size

        self.scale += delta_scale

        new_point_x = int(ratio_x * self.cell_size)
        new_point_y = int(ratio_y * self.cell_size)

        self.x += point_x - new_point_x
        self.y += point_y - new_point_y

        self.surface = pygame.transform.scale(self.surface, (self.width, self.height))

    def check_boundaries(self):
        if self.width < self.parent.width or \
           self.height < self.parent.height:
            return

        if self.x > 0:
            self.x = 0
        elif self.x < -self.width + self.parent.width:
            self.x = -self.width + self.parent.width

        if self.y > 0:
            self.y = 0
        elif self.y < -self.height + self.parent.height:
            self.y = -self.height + self.parent.height

    @property
    def is_just_pressed(self):
        # If not input provided or the delta is small return False
        # Otherwise returns the position of touching
        if not self.full_delta:
            return False

        if self.full_delta[0]**2 + self.full_delta[1]**2 > 25**2:
            # Delta lenght is not more than 5 = sqrt(25) pixels
            return False

        return pygame.mouse.get_pos()
