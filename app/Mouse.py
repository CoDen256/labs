import pygame


class Mouse(pygame.sprite.Sprite):
    def __init__(self):
        super().__init__()

        self.surface = pygame.Surface((1, 1))
        self.rect = self.surface.get_rect()
        self.pressed = False

    def on_button_down(self):
        self.pressed = True

    def on_button_up(self):
        self.pressed = False

    def update(self):
        self.rect.center = pygame.mouse.get_pos()

    @property
    def position(self):
        return self.rect.center

    @property
    def x(self):
        return self.position[0]

    @property
    def y(self):
        return self.position[1]
