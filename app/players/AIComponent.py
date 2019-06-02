import doctest
import sys
import math
import os
import pickle
import base64
import subprocess
import platform

from random import choice 
from copy import deepcopy

if sys.version_info < (3, 5):
    math.inf = float('inf')

infinity = math.inf


class AIComponent:
    '''
    >>> ai = AIComponent(1)
    >>> grid = [
    ...     [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    ...     [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    ...     [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    ...     [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    ...     [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    ...     [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    ...     [0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 0, 0, 0, 0, 0],
    ...     [0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0],
    ...     [0, 0, 0, 0, 0, 0, 1, 1, -1, 0, 0, 0, 0, 0, 0],
    ...     [0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0],
    ...     [0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0],
    ...     [0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0],
    ...     [0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0],
    ...     [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0],
    ...     [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    ... ]
    >>> res = ai.compute_next_move_exp(grid)
    >>> res
    2

    '''
    def __init__(self, id):
        self._human = id * -1
        self._computer = id

    @property
    def human(self):
        return self._human

    @property
    def computer(self):
        return self._computer

    def compute_next_move_3x3(self, grid):
        depth = len(self.__empty_cells(grid))

        #here im returning True if comp won and False otherwise
        if depth == 0 and self.__is_over(grid):
            return self.__is_won(grid, self._computer)
        if  self.__is_over(grid):
            return self.__is_won(grid, self._computer)

        #here im returning None in case of draw
        if depth == 0:
            return

        if depth == 9:
            x = choice([0, 1, 2])
            y = choice([0, 1, 2])
        else:
            move = self.__minimax_algorithm(grid, depth, self._computer)
            x, y = move[0], move[1]

        #coordinates
        return x, y

    def __minimax_algorithm(self, grid, depth, player):
        if player == self._computer:
            best = [-1, -1, -infinity]
        else:
            best = [-1, -1, +infinity]

        if depth == 0 or self.__is_over(grid):
            score = self.__change_score(grid)
            return [-1, -1, score]

        empty_cells = self.__empty_cells(grid)

        for cell in empty_cells:
            x, y = cell[0], cell[1]
            grid[x][y] = player
            score = self.__minimax_algorithm(grid, depth - 1, -player)
            grid[x][y] = 0
            score[0], score[1] = x, y

            if player == self._computer:
                if score[2] > best[2]:
                    best = score
            else:
                if score[2] < best[2]:
                    best = score

        return best

    def __empty_cells(self, grid):
        empty_cells = []

        for x, row in enumerate(grid):
            for y, cell in enumerate(row):
                if cell == 0:
                    empty_cells.append([x, y])

        return empty_cells

    def __is_won(self, grid, player):
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

    def __is_over(self, grid):
        return self.__is_won(grid, self._human) or self.__is_won(grid, self._computer)

    def __change_score(self, grid):
        if self.__is_won(grid, self._computer):
            score = +1
        elif self.__is_won(grid, self._human):
            score = -1
        else:
            score = 0

        return score

    def compute_next_move_exp(self, grid):
        grid_pickled = pickle.dumps(grid)
        grid_pickled = base64.b64encode(grid_pickled).decode()

        player_pickled = pickle.dumps(self._computer)
        player_pickled = base64.b64encode(player_pickled).decode()

        args = lambda inter: [
                os.path.join(os.path.dirname(os.path.abspath(__file__)), *inter),
                os.path.join(
                    os.path.dirname(os.path.abspath(__file__)),
                    'unlimited.py'
                ),
                grid_pickled,
                player_pickled
            ]

        result_pickled = None

        if platform.system() == "Windows":
            result_pickled = subprocess.Popen(
                args(['pypy3', 'pypy3.exe']),
                stdout=subprocess.PIPE,
                universal_newlines=True
            ).communicate()[0]

        else:
            result_pickled = subprocess.run(
                args(['pypy', 'bin', 'pypy3']),
                stdout=subprocess.PIPE
            ).stdout

        result_pickled = base64.b64decode(result_pickled)
        result = pickle.loads(result_pickled)
        if result[0] != None:
            return result[0][0], result[0][1]
        else:
            if result[1] == 1e7:
                return True
            else:
                return False  


if __name__ == '__main__':
    doctest.testmod()
