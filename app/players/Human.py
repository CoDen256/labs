from players.Player import Player
import pygame
from hud.Mouse import Mouse


class Human(Player):
    def __init__(self, grid, id):
        super().__init__(grid, id)
        self.pressed = False

    def onTurn(self):
        pos = self.handleInput()

        if pos is None:
            return

        return self.makeMove(pos)

    def makeMove(self, position):
        # Convert position from absolute to cell position
        # Returns true if move is successful and cell is empty
        result_pos = self.grid.convert(position)
        if result_pos:
            return self.grid.add(self.id, result_pos)
        else:
            return False

    def handleInput(self):
        # Handle the input user provides and returns the position
        # If not input provided returns None
        if Mouse.is_pressed() and not self.pressed:
            self.pressed = True
            return pygame.mouse.get_pos()
        if not Mouse.is_pressed():
            self.pressed = False

        return None
        
