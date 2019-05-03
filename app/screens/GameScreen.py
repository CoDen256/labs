import pygame

from random import randint

from utils import *
from grids.ExpandableGrid import ExpandableGrid
from grids.StaticGrid import StaticGrid
from players.Human import Human
from players.Computer import Computer
from screens.surfaces.GameSurface import GameSurface


class GameScreen:
    def __init__(self, game, type):
        self.game = game
        self.type = type
        # type == 0 - Static
        # type == 1 - Expandable

    def create(self):

        self.surface = GameSurface((0, 0), self.game.size)

        log("GameScreen size", self.surface.get_size())

        self.grid = [StaticGrid(self.surface, (210, 50), cell_size=220),
                     ExpandableGrid(self.surface, (50, 50), cell_size=220)][self.type]

        self.players = [Human(self.grid, 1), Human(self.grid, -1)] 
        # num = (id + 2) % 3

        self.currentPLayerNum = randint(0, 1)
        self.update_current()

    def handle_input(self):
        for e in pygame.event.get():
            self.grid.update_input(e)
            if e.type == pygame.QUIT:
                self.game.quit()

            if e.type == pygame.KEYDOWN:
                if e.key == pygame.K_ESCAPE:
                    self.game.quit()

                if e.key == pygame.K_SPACE:
                    self.game.change_screen(self.game.screen("MainMenuScreen")(self.game))

    def update(self):
        self.update_current()
        self.grid.update()

        if not self.current.on_turn():
            pass
        else:
            self.grid.update_score()
            self.currentPLayerNum = 1 - self.currentPLayerNum

    def render(self):
        self.surface.render(self.game.window)
        self.surface.fill((220, 220, 220))

        self.grid.highlight(pygame.mouse.get_pos())
        self.grid.render()

        #toast(self.surface, str(pygame.mouse.get_pos()), 20, (50, 50, 50), self.game.w * 2/3, 20)
        #toast(self.surface, str(self.grid.scale), 20, (50, 50, 50), self.game.w * 1/3, 20)
        # self.renderMessages()

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
