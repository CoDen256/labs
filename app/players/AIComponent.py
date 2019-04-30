import doctest
import sys
import math

if sys.version_info < (3, 5):
    math.inf = float('inf')

infinity = math.inf


class AIComponent:
    '''
    >>> ai = AIComponent(1)
    >>> grid = [
    ...     [1, 1, -1],
    ...     [0, 0, -1],
    ...     [-1, -1, 1],
    ... ]
    >>> ai.compute_next_move_3x3(grid)
    >>> grid
    [[1, 0, 1],
    [0, -1, 0],
    [-1, 0, 0]]

    '''
    def __init__(self, id):
        self.human = id * -1
        self.computer = id

    def compute_next_move_3x3(self, grid):
        depth = len(self.empty_cells(grid))
        if depth == 0 or self.is_over(grid):
            return

        if depth == 9:
            x = choice([0, 1, 2])
            y = choice([0, 1, 2])
        else:
            move = self.minimax_algorithm(grid, depth, self.computer)
            x, y = move[0], move[1]

        self.set_move(x, y, self.computer, grid)

        # Has to return position

    def compute_next_move_exp(self, grid):
        # For expandable grid
        pass

    def minimax_algorithm(self, grid, depth, player):
        if player == self.computer:
            best = [-1, -1, -infinity]
        else:
            best = [-1, -1, +infinity]

        if depth == 0 or self.is_over(grid):
            score = self.change_score(grid)
            return [-1, -1, score]

        empty_cells = self.empty_cells(grid)

        for cell in empty_cells:
            x, y = cell[0], cell[1]
            grid[x][y] = player
            score = self.minimax_algorithm(grid, depth - 1, -player)
            grid[x][y] = 0
            score[0], score[1] = x, y

            if player == self.computer:
                if score[2] > best[2]:
                    best = score
            else:
                if score[2] < best[2]:
                    best = score

        return best

    def empty_cells(self, grid):
        empty_cells = []

        for x, row in enumerate(grid):
            for y, cell in enumerate(row):
                if cell == 0:
                    empty_cells.append([x, y])

        return empty_cells

    def is_won(self, grid, player):
        win_state = [
            [grid[0][0], grid[0][1], grid[0][2]],
            [grid[1][0], grid[1][1], grid[1][2]],
            [grid[2][0], grid[2][1], grid[2][2]],
            [grid[0][0], grid[1][0], grid[2][0]],
            [grid[0][1], grid[1][1], grid[2][1]],
            [grid[0][2], grid[1][2], grid[2][2]],
            [grid[0][0], grid[1][1], grid[2][2]],
            [grid[2][0], grid[1][1], grid[0][2]],
        ]
        if [player, player, player] in win_state:
            return True
        else:
            return False

    def is_over(self, grid):
        return self.is_won(grid, self.human) or self.is_won(grid, self.computer)

    def set_changed_cell(self, x, y, player, grid):
        if self.is_correct_move(x, y, grid):
            grid[x][y] = player
            return True
        else:
            return False

    def is_correct_move(self, x, y, grid):
        empty_cells = self.empty_cells(grid)

        if [x, y] in empty_cells:
            return True
        else:
            return False

    def change_score(self, grid):
        if self.is_won(grid, self.computer):
            score = +1
        elif self.is_won(grid, self.human):
            score = -1
        else:
            score = 0

        return score


if __name__ == '__main__':
    doctest.testmod()
