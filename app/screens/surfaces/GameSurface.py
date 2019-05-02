import pygame


class GameSurface(pygame.Surface):
    def __init__(self, position, size):
        super().__init__(size)
        self.position = self.x, self.y = position

    def render(self, parent):
        parent.blit(self, self.position)
