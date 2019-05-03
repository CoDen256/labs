from players.Player import Player
import pygame


class Human(Player):
    def __init__(self, grid, id):
        super().__init__(grid, id)
        self.pressed = False

    def on_turn(self):
        # Handle the input user and returns the position
        pos = self.grid.is_just_pressed

        if not pos:
            return False

        return self.make_move(pos)

    def make_move(self, position):
        # Convert position from absolute to cell position
        # Returns true if move is successful and cell is empty

        result_pos = self.grid.convert(position)
        if result_pos:
            return self.grid.add(self.id, result_pos)
        else:
            return False
