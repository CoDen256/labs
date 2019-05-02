from grids.Grid import Grid
import pygame


class ExpandableGrid(Grid):
    def __init__(self, parent, position, cell_size, columns=25, rows=25):
        super().__init__(parent, columns, rows, cell_size, position, scale=0.5)
        self.scores = [0, 0]
        self.position = self.x, self.y = self.parent.x - self.x, self.parent.y - self.y

        self.last_pressed = None
        self.delta = None

        self.first = None
        self.full_delta = None

    def updateScore(self):
        pass

    def updateInput(self, event):
        if event.type == pygame.MOUSEBUTTONDOWN:
            if not self.dragged:
                self.dragged = True
                self.last_pressed = self.first = pygame.mouse.get_pos()

        if event.type == pygame.MOUSEBUTTONUP:
            self.dragged = False

            self.full_delta = self.first[0] - pygame.mouse.get_pos()[0], self.first[1] - pygame.mouse.get_pos()[1]

            self.delta = None
            self.last_pressed = None
            

    
    def update(self):
        if self.dragged:
            self.delta = pygame.mouse.get_pos()[0] - self.last_pressed[0], pygame.mouse.get_pos()[1] - self.last_pressed[1]
            self.last_pressed = pygame.mouse.get_pos()

            self.x = self.x + self.delta[0]
            self.y = self.y + self.delta[1]



    def render(self):
        self.parent.blit(self.surface, (self.x, self.y))

        self.renderGrid()
        self.renderCells()

    #@property
    #def is_just_pressed(self):
    #    return not self.dragged or (self.dragged and )

