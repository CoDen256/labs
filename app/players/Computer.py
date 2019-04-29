from players.Player import Player


class Computer(Player):
    def __init__(self, grid, id):
        super(self, Player).__init__(grid, id)
        self.ai_component = AIComponent()

    def onTurn():
        updateAI()

        position = self.ai_component.compute_next_move()
        self.makeMove(position)

    def makeMove(position):
        self.grid.add(position)

    def updateAI(self):
        self.ai_component.update(self.grid)