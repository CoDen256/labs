from abc import ABCMeta, abstractmethod
import pygame


class Grid():
    __metaclass__ = ABCMeta

    def __init__(self, parent, columns, rows, cell_size, position):
        self.cells = [[0 for i in range(columns)] for j in range(rows)]
        self.cell_size = cell_size

        self.columns = columns
        self.rows = rows

        self.width = cell_size * columns
        self.height = cell_size * rows

        self.position = self.x, self.y = position

        self.parent = parent  # parent surface
        self.surface = pygame.Surface((self.width, self.height))


    @abstractmethod
    def updateInput(self):
        """ Updates input of user (zoom in/out, drag)"""
        pass

    @abstractmethod
    def updateScore(self):
        """ Updates score of each player """
        pass

    @abstractmethod
    def convert(self, position):
        """ Converts absolute position to array position -> (x,y) """
        pass

    @abstractmethod
    def add(self, value, position):
        """ Adds an input to cells at position (relative to array) """
        pass

    @abstractmethod
    def render(self):
        """Renders the particular grid and its cells to a surface """
        pass

    @abstractmethod
    def highlight(self, position):
        """ Highlights certain cell if hovered """
        pass

    
