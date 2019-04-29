from abc import ABCMeta, abstractmethod


class Player:

    def __init__(self, grid, id):
        self.grid = grid
        self.id = id

    @abstractmethod
    def onTurn(self):
        """ Methods that is called on each turn  -> bool"""
        pass

    @abstractmethod
    def makeMove(self):
        """ Methods that is called to make a move on a grid """
        pass
