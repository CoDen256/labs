import pygame

from random import randint

from utils import *
from grids.ExpandableGrid import ExpandableGrid
from grids.StaticGrid import StaticGrid
from players.Human import Human
from players.Computer import Computer
from screens.surfaces.GameSurface import GameSurface


class GameScreen:
    def __init__(self, game, type, mode):
        self.game = game
        self.type = type
        self.mode = mode
        # type == 0 - Static
        # type == 1 - Expandable
        # mode == 0 - Human
        # mode == 1 - AI

    def create(self):

        self.surface = GameSurface((0, 0), self.game.size)

        print("GameScreen size", self.surface.get_size())

        self.grid = [StaticGrid, ExpandableGrid][self.type](self.surface, (210, 50), cell_size=220)

        self.players = [Human(self.grid, 1), [Human, Computer][self.mode](self.grid, -1)]
        # num = (id + 2) % 3
        
        self.victory_message = ""
        self.currentPLayerNum = 0 if self.mode and self.type else randint(0, 1)
        self.update_current()

    def handle_input(self):
        for e in pygame.event.get():
            self.grid.update_input(e)
            if e.type == pygame.QUIT:
                self.game.quit()

            if e.type == pygame.KEYDOWN:
                if e.key == pygame.K_c:
                    self.game.change_color()
                if e.key == pygame.K_ESCAPE:
                    self.game.set(self.game.screen("MainMenuScreen")(self.game))

    def update(self):
        if self.victory_message:
            Utils.toast(self.surface,
                        self.victory_message,
                        40, color.victory_text, self.game.w/2, self.game.h/2)
            return

        self.update_current()
        self.grid.update()

        if not self.current.on_turn():
            pass
        else:
            result = self.grid.update_score()
            if result != 0:
                if result != -2:  # not draw
                    self.victory_message = "Player #{} - {} has won".format(self.currentPLayerNum, self.current)
                else:
                    self.victory_message = "Draw!"
            else:
                self.currentPLayerNum = 1 - self.currentPLayerNum

    def render(self):
        self.surface.render(self.game.window)
        self.surface.fill(color.background_game)

        self.grid.highlight(pygame.mouse.get_pos())
        self.grid.render()

        self.renderMessages()

        pygame.display.flip()

    def renderMessages(self):
        Utils.toast(self.surface,
                    "Now is turn of Player #{} - {}".format(self.currentPLayerNum, self.current),
                    20, color.text, self.game.w/2, 20)

    def update_current(self):
        self.current = self.players[self.currentPLayerNum]
