import pygame


class GameSurface(pygame.Surface):
    def __init__(self, position, size):
        super().__init__(size)
        self.position = self.x, self.y = position
        self.width, self.height = self.size = size

    def render(self, parent):
        parent.blit(self, self.position)
