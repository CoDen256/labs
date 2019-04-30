from players.AIComponent import AIComponent
from players.Player import Player
from grids.ExpandableGrid import ExpandableGrid


class Computer(Player):
    def __init__(self, grid, id):
        super().__init__(grid, id)
        self.ai_component = AIComponent(id)

    def onTurn(self):
        position = None
        if isinstance(self.grid, ExpandableGrid):
            position = self.ai_component.compute_next_move(self.grid)
        else:
            position = self.ai_component.compute_next_move_exp(self.grid)
        self.makeMove(position)
        
        return True

    def makeMove(self, position):
        self.grid.add(self.id, position)

