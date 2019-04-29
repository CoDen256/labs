import pygame
from random import randint
from utils import *
from grids.ExpandableGrid import ExpandableGrid
from grids.StaticGrid import StaticGrid
from players.Human import Human
from players.Computer import Computer


class GameScreen:
    def __init__(self, game):
        self.game = game
        self.type = self.game.type
        # type == 0 - Static
        # type == 1 - Expandable
    
    def create(self):
        self.surface = pygame.Surface(self.game.size)

        self.grid = [StaticGrid(), ExpandableGrid()][self.type]
        self.player0 = Human(self.grid, 0)
        self.player1 = Human(self.grid, 1)

        self.players = [self.player0, self.player1]

        self.currentId = randint(0, 1)
        self.current = self.players[self.currentId]

    def handleInput(self):
        for e in pygame.event.get():
            if e.type == pygame.QUIT:
                self.game.quit()

            if e.type == pygame.KEYDOWN:
                if e.key == pygame.K_ESCAPE:
                    self.game.quit()
                
                if e.key == pygame.K_SPACE:
                    self.game.change_screen("MainMenu")

    def update(self):
        self.grid.updateInput()
        self.current = self.players[self.currentId]

        if not self.current.onTurn():
            pass
        else:
            self.grid.updateScore()
            self.currentId = 1 - self.currentId

    def render(self):
        self.game.window.blit(self.surface, (0, 0))
        self.surface.fill((100, 150, 175))
        
        toast(self.surface,
              "Welcome To GameScreen with {} Grid".format(self.type),
              20, (50, 50, 50), self.game.w/2, self.game.h/2)

        pygame.display.flip()