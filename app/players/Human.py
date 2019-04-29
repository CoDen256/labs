from players.Player import Player


class Human(Player):
    def __init__(self, grid, id):
        super().__init__(grid, id)

    def onTurn(self):
        pos = self.handleInput()

        if pos is None:
            return

        return makeMove(position)

    def makeMove(self, position):
        # Convert position from absolute to cell position
        # Returns true if move is successful and cell is empty
        pass

    def handleInput(self):
        # Handle the input user provides and returns the position
        # If not input provided returns None
        pass
