import pygame
import pygame.gfxdraw
from utils import *


class Button(pygame.sprite.Sprite):
    def __init__(self, position, size, color, text, text_color=(25, 25, 25), text_size=30, on_click=None):
        self.position = self.x, self.y = position
        self.size = self.width, self.height = size
        self.color = color

        self.text = text
        self.text_color = text_color
        self.text_size = text_size

        self.image = pygame.Surface(size)

        self.rect = self.image.get_rect()
        self.rect.center = position

        self.on_click = on_click

    def update(self, mouse):
        self.draw_borders(mouse)

        if self.hover(mouse):
            self.check_pressing(mouse)

    def render(self, surface):
        surface.blit(self.image, (self.x-self.width//2, self.y-self.height//2))

        self.image.fill(self.color)


        Utils.toast(self.image, self.text, self.text_size, self.text_color,
              self.width/2, self.height/2)

    def check_pressing(self, mouse):
        if mouse.pressed:
            self.on_click()

    def draw_borders(self, mouse):
        if self.hover(mouse):
            pygame.draw.rect(self.image, (250, 250, 250),
                             [1.5, 1.5, self.width-1.5, self.height-1.5], 3)
        else:
            pygame.draw.rect(self.image, (50, 50, 50),
                             [0, 0, self.width, self.height], 3)

    def hover(self, mouse):
        return pygame.sprite.collide_rect(mouse, self)
