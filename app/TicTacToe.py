import pygame

import screens.screen_manager as screen_manager
from utils import *


class TicTacToe:

    def __init__(self, w, h):
        self.size = self.width, self.height = self.w, self.h = w, h
        self.window = pygame.display.set_mode(self.size)
        self.current_screen = None
        self.mainloop = None

        pygame.init()

    def run(self, screen):
        self.current_screen = screen
        self.current_screen.create()

        self.mainloop = True
        self.update()

    def update(self):
        while self.mainloop:
            self.current_screen.handle_input()
            self.current_screen.update()
            self.current_screen.render()

    def set(self, screen):
        self.mainloop = False
        self.run(screen)

    def quit(self):
        self.mainloop = False
        quit_game()

    def screen(self, name):
        return screen_manager.screens[name]

game = TicTacToe(1080, 720)
game.run(game.screen("MainMenuScreen")(game))
