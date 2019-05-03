from abc import ABCMeta, abstractmethod


class Player:

    def __init__(self, grid, id):
        self.grid = grid
        self.id = id

    @abstractmethod
    def on_turn(self):
        """ Methods that is called on each turn -> bool"""
        pass

    @abstractmethod
    def make_move(self):
        """ Methods that is called to make a move on a grid """
        pass
