from players.AIComponent import AIComponent
from players.Player import Player


class Computer(Player):
    def __init__(self, grid, id):
        super().__init__(grid, id)
        self.ai_component = AIComponent(id)

    def onTurn(self):
        position = self.ai_component.compute_next_move()
        self.makeMove(position)
        
        return True

    def makeMove(self, position):
        self.grid.add(self.id, position)

