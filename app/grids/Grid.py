from abc import ABCMeta, abstractmethod


class Grid():
    __metaclass__ = ABCMeta

    def __init__(self, surface):
        self.surface = surface

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

    
