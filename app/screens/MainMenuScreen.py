import pygame

from utils import *
from Mouse import Mouse
from buttons.Button import Button


class MainMenuScreen:
    def __init__(self, game):
        self.game = game

    def create(self):
        self.surface = pygame.Surface(self.game.size)

        self.mouse = Mouse()
        self.button_3x3 = Button(
                            position=(self.game.w/3, self.game.h/2),
                            size=(300, 150),
                            color=(230, 130, 100),
                            text="3x3 Grid",
                            on_click=lambda: self.game.set(self.game.screen("SelectionScreen")(self.game, 0)))

        self.button_inf = Button(
                            position=(self.game.w*2/3, self.game.h/2),
                            size=(300, 150),
                            color=(230, 130, 100),
                            text="Infinite Grid",
                            on_click=lambda: self.game.set(self.game.screen("SelectionScreen")(self.game, 1)))

    def handle_input(self):
        for e in pygame.event.get():
            if e.type == pygame.QUIT:
                self.game.quit()

            if e.type == pygame.MOUSEBUTTONDOWN:
                self.mouse.on_button_down()

            if e.type == pygame.MOUSEBUTTONUP:
                self.mouse.on_button_up()

            if e.type == pygame.KEYDOWN:
                if e.key == pygame.K_ESCAPE:
                    self.game.quit()


    def update(self):
        self.mouse.update()
        self.button_inf.update(self.mouse)
        self.button_3x3.update(self.mouse)

    def render(self):
        self.game.window.blit(self.surface, (0, 0))
        self.surface.fill((100, 150, 175))

        self.button_inf.render(self.surface)
        self.button_3x3.render(self.surface)

        toast(self.surface, "Choose the grid", 20, (50, 50, 50),
              self.game.w/2, self.game.h/3)

        pygame.display.flip()
