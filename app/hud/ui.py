import pygame


class HUD(pygame.Surface):
    def __init__(self, size, scores):
        super().__init__(size)
        self.size = size
        self.scores = scores