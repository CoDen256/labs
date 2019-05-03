from players.AIComponent import AIComponent
from players.Player import Player
from grids.ExpandableGrid import ExpandableGrid


class Computer(Player):
    def __init__(self, grid, id):
        super().__init__(grid, id)
        self.ai_component = AIComponent(id)

    def on_turn(self):
        position = None

        if isinstance(self.grid, ExpandableGrid):
            position = self.ai_component.compute_next_move_exp(self.grid)
        else:
            position = self.ai_component.compute_next_move_3x3(self.grid)

        self.make_move(position)

        return True

    def make_move(self, position):
        self.grid.add(self.id, position)
