import sys
import pygame


class Utils:
    def toast(surface, text, size, color, x, y, font="Arial Black", angle=0):

        if font.endswith('.otf') or font.endswith('.ttf'):
            font = pygame.font.Font(font, size)
        else:
            font = pygame.font.SysFont(font, size)

        textS = font.render(text, True, color)
        textR = font.render(text, True, color).get_rect()

        textS = pygame.transform.rotate(textS, angle)

        textR.center = x, y
        surface.blit(textS, textR)

    def quit_game():
        sys.exit()
