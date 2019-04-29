from abc import ABCMeta, abstractmethod


class Grid():
    __metaclass__ = ABCMeta

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
        """ Converts absolute position to grid position -> (x,y) """
        pass

    @abstractmethod
    def add(self, value, position):
        """ Adds an input to position (relative to grid) """
        pass
