import pygame

from random import randint

from utils import *
from grids.ExpandableGrid import ExpandableGrid
from grids.StaticGrid import StaticGrid
from players.Human import Human
from players.Computer import Computer
from hud.ui import HUD
from hud.Mouse import Mouse


class GameScreen:
    def __init__(self, game):
        self.game = game
        self.type = self.game.type
        # type == 0 - Static
        # type == 1 - Expandable

    def create(self):

        self.surface = pygame.Surface(self.game.size)
        log("GameScreen size", self.surface.get_size())

        self.grid = [StaticGrid(self.surface, x=210, y=50, cell_size=220),
                     ExpandableGrid()][self.type]

        self.players = [Human(self.grid, 1), Human(self.grid, -1)] 
        # num = (id + 2) % 3

        self.currentPLayerNum = randint(0, 1)
        self.update_current()

    def handleInput(self):
        self.grid.updateInput()
        for e in pygame.event.get():
            if e.type == pygame.QUIT:
                self.game.quit()

            if e.type == pygame.KEYDOWN:
                if e.key == pygame.K_ESCAPE:
                    self.game.quit()

                if e.key == pygame.K_SPACE:
                    self.game.change_screen("MainMenu")

    def update(self):
        self.update_current()

        if not self.current.onTurn():
            pass
        else:
            self.grid.updateScore()
            self.currentPLayerNum = 1 - self.currentPLayerNum

    def render(self):
        self.game.window.blit(self.surface, (0, 0))
        self.surface.fill((220, 220, 220))

        self.grid.highlight(pygame.mouse.get_pos())
        self.grid.render()

        self.renderMessages()

        pygame.display.flip()

    def renderMessages(self):
        toast(self.surface,
              "Welcome To GameScreen with {} Grid".format(self.type),
              20, (50, 50, 50), self.game.w/2, 20)

        toast(self.surface,
              "Player #{} has current turn".format(self.currentPLayerNum),
              15, (50, 50, 50), self.game.w - 150, 20)

    def update_current(self):
        self.current = self.players[self.currentPLayerNum]
