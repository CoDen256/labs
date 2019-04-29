import pygame
from utils import *


class MainMenuScreen:
    def __init__(self, game):
        self.game = game
        self.game.type = None

    def create(self):
        self.surface = pygame.Surface(self.game.size)

    def handleInput(self):
        for e in pygame.event.get():
            if e.type == pygame.QUIT:
                self.game.quit()

            if e.type == pygame.KEYDOWN:
                if e.key == pygame.K_ESCAPE:
                    self.game.quit()
                
                if e.key == pygame.K_0:
                    self.game.type = 0
                    self.game.change_screen("Game")
                if e.key == pygame.K_1:
                    self.game.type = 1
                    self.game.change_screen("Game")

    def update(self):
        pass

    def render(self):
        self.game.window.blit(self.surface, (0, 0))
        self.surface.fill((100, 150, 175))

        toast(self.surface, "Welcome To MainMenuScreen", 20, (50, 50, 50),
              self.game.w/2, self.game.h/2)

        pygame.display.flip()
