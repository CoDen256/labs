import pygame
from utils import *
from Mouse import Mouse
from buttons.Button import Button


class SelectionScreen:
    def __init__(self, game, type):
        self.game = game
        self.type = type

    def create(self):
        self.surface = pygame.Surface(self.game.size)

        self.mouse = Mouse()
        self.button_hum = Button(
                            position=(self.game.w/3, self.game.h/2),
                            size=(300, 150),
                            color=(230, 130, 100),
                            text="Against Human",
                            on_click=lambda: self.game.set(self.game.screen("GameScreen")(self.game, self.type, 0)))

        self.button_ai = Button(
                            position=(self.game.w*2/3, self.game.h/2),
                            size=(300, 150),
                            color=(230, 130, 100),
                            text="Against AI",
                            on_click=lambda: self.game.set(self.game.screen("GameScreen")(self.game, self.type, 1)))

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
        self.button_hum.update(self.mouse)
        self.button_ai.update(self.mouse)

    def render(self):
        self.game.window.blit(self.surface, (0, 0))
        self.surface.fill((100, 150, 175))

        self.button_hum.render(self.surface)
        self.button_ai.render(self.surface)

        Utils.toast(self.surface, "Select your opponent", 20, (50, 50, 50),
                    self.game.w/2, self.game.h/3)

        pygame.display.flip()
