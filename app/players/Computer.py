from players.Player import Player


class Computer(Player):
    def __init__(self, grid, id):
        super(Computer, self).__init__(grid, id)
        self.ai_component = AIComponent()

    def onTurn(self):
        self.ai_component.update(self.grid)

        position = self.ai_component.compute_next_move()
        self.makeMove(position)
        
        return True

    def makeMove(self, position):
        self.grid.add(self.id, position)

