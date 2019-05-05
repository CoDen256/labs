from players.AIComponent import AIComponent
from players.Player import Player
from grids.ExpandableGrid import ExpandableGrid
from utils import *


class Computer(Player):
    def __init__(self, grid, id):
        super().__init__(grid, id)
        self.ai_component = AIComponent(id)
        self.time = 0
        self.thinking_time = 2

    def __repr__(self):
        return "Computer"

    def on_turn(self):
        if self.thinking():
            position = None
            if isinstance(self.grid, ExpandableGrid):
                position = self.ai_component.compute_next_move_exp(self.grid.cells)
            else:
                position = self.ai_component.compute_next_move_3x3(self.grid.cells)

            self.make_move(position)

            return True
        return False

    def thinking(self):
        self.time += 1/60

        toast(self.grid.parent, "Let me think...", 20, (100, 100, 100), self.grid.parent.width-100, 50)

        ready = self.time >= self.thinking_time
        if ready:
            self.time = 0

        return ready


    def make_move(self, position):
        if position:
            self.grid.add(self.id, position)
