import pickle
import base64
from sys import argv
from random import shuffle


def compute_best_move(grid, player, depth=2):
    winner = compute_winner(grid)
    if winner == player:
        return None, 1e7
    elif winner == -player:
        return None, -1e7

    if depth == 0:
        mark = estimation(grid, player)

        return None, mark

    else:
        possible_moves = compute_possible_moves(grid)

        best_move, best_mark = None, -1e8

        for current_move in possible_moves:
            grid[current_move[0]][current_move[1]] = player

            _, current_mark = compute_best_move(grid, -player, depth=depth - 1)
            current_mark = -current_mark

            if current_mark > best_mark:
                best_mark = current_mark
                best_move = current_move

            grid[current_move[0]][current_move[1]] = 0

        return best_move, best_mark


def estimation(grid, player):
    score = 0

    rows = make_rows(grid)

    for row in rows:
        score += (
            row.count('+') * 1 +
            row.count('++') * 10 +
            row.count('+++') * 100 +
            row.count('++++') * 1000 +
            row.count('+++++') * 10000
        )
        score -= (
            row.count('-') * 1 +
            row.count('--') * 10 +
            row.count('---') * 100 +
            row.count('----') * 1000 +
            row.count('-----') * 10000
        )

    return score * player


def compute_winner(grid):
    rows = make_rows(grid)

    for row in rows:
        if '+++++' in row:
            return 1
        elif '-----' in row:
            return -1

    return None


def compute_possible_moves(grid):
    moves = set()

    for center_row in range(len(grid)):
        for center_column in range(len(grid)):
            if grid[center_row][center_column]:
                for row in range(max(center_row - 4, 0), min(center_row + 5, len(grid))):
                    for column in range(max(center_column - 4, 0), min(center_column + 5, len(grid))):
                        if not grid[row][column]:
                            moves.add((row, column))

    moves = list(moves)
    shuffle(moves)

    return moves


def to_symbol(cell):
    if cell > 0:
        return '+'
    elif cell < 0:
        return '-'
    else:
        return ' '


def make_rows(grid):
    rows = []

    rows.extend(''.join(to_symbol(cell) for cell in row) for row in grid)
    rows.extend(''.join(to_symbol(cell) for cell in row) for row in zip(*grid))

    start_points = (
        [(0, column) for column in range(len(grid) - 4)] + 
        [(row, 0) for row in range(1, len(grid) - 4)]
    )
    for start_point in start_points:
        row, column = start_point
        new_row = []

        while row < len(grid) and column < len(grid):
            new_row.append(grid[row][column])
            row += 1
            column += 1

        rows.append(''.join(to_symbol(cell) for cell in new_row))

    start_points = (
        [(0, column) for column in range(4, len(grid))] + 
        [(row, len(grid) - 1) for row in range(1, len(grid) - 4)]
    )
    for start_point in start_points:
        row, column = start_point
        new_row = []

        while row < len(grid) and column >= 0:
            new_row.append(grid[row][column])
            row += 1
            column -= 1

        rows.append(''.join(to_symbol(cell) for cell in new_row))

    return rows


if __name__ == '__main__':
    grid_pickled = argv[1]
    grid_pickled = grid_pickled.encode()
    grid_pickled = base64.b64decode(grid_pickled)
    grid = pickle.loads(grid_pickled)

    player_pickled = argv[2]
    player_pickled = player_pickled.encode()
    player_pickled = base64.b64decode(player_pickled)
    player = pickle.loads(player_pickled)

    result = compute_best_move(grid, player)

    result_pickled = pickle.dumps(result)
    result_pickled = base64.b64encode(result_pickled).decode()

    print(result_pickled)
