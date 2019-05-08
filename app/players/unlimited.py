from copy import deepcopy
from pprint import pprint


grid = [[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 1, -1, -1, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
]


def compute_best_move(grid, player, depth=2):
    if is_game_over(grid, player):
        return None, 1e9
    elif is_game_over(grid, -player):
        return None, -1e9

    if depth == 0:
        mark = estimation(grid, player)

        return None, mark

    else:
        possible_moves = compute_possible_moves(grid)

        best_move, best_mark = None, -1e10

        for current_move in possible_moves:
            current_grid = deepcopy(grid)
            current_grid[current_move[0]][current_move[1]] = player
            _, current_mark = compute_best_move(current_grid, -player, depth=depth-1)
            current_mark = -current_mark
            if current_mark > best_mark:
                best_mark = current_mark
                best_move = current_move

        return best_move, best_mark


def estimation(grid, player):
    grid_score = 0

    # horizontally
    for i in range(len(grid)):
        for j in range(len(grid) - 4):
            cells = grid[i][j:j + 5]
            if -player not in cells:
                if player in cells:
                    index = 0
                    max_sc = 0
                    cur_score = 0
                    while index < 5:
                        if cells[index] == player:
                            cur_score += 1
                        else:
                            if cur_score > max_sc:
                                max_sc = cur_score
                            cur_score = 0
                        index += 1
                    if cur_score > max_sc:
                        max_sc = cur_score
                    current_score =  10 ** (cells.count(player) - 1) + (max_sc * (10 ** (cells.count(player) - 1)))
                    grid_score += current_score
            if player not in cells:
                if -player in cells:
                    index = 0
                    max_sc = 0
                    cur_score = 0
                    while index < 5:
                        if cells[index] == -player:
                            cur_score += 1
                        else:
                            if cur_score > max_sc:
                                max_sc = cur_score
                            cur_score = 0
                        index += 1
                    if cur_score > max_sc:
                        max_sc = cur_score
                    current_score =  10 ** (cells.count(-player) - 1) + (max_sc * (10 ** (cells.count(-player) - 1)))
                    grid_score -= current_score

    # vertically
    grid = [list(row) for row in zip(*grid)]
    for i in range(len(grid)):
        for j in range(len(grid) - 4):
            cells = grid[i][j:j + 5]

            if -player not in cells:
                if player in cells:
                    index = 0
                    max_sc = 0
                    cur_score = 0
                    while index < 5:
                        if cells[index] == player:
                            cur_score += 1
                        else:
                            if cur_score > max_sc:
                                max_sc = cur_score
                            cur_score = 0
                        index += 1
                    if cur_score > max_sc:
                        max_sc = cur_score
                    current_score =  10 ** (cells.count(player) - 1) + (max_sc * (10 ** (cells.count(player) - 1)))
                    grid_score += current_score
            if player not in cells:
                if -player in cells:
                    index = 0
                    max_sc = 0
                    cur_score = 0
                    while index < 5:
                        if cells[index] == -player:
                            cur_score += 1
                        else:
                            if cur_score > max_sc:
                                max_sc = cur_score
                            cur_score = 0
                        index += 1
                    if cur_score > max_sc:
                        max_sc = cur_score
                    current_score =  10 ** (cells.count(-player) - 1) + (max_sc * (10 ** (cells.count(-player) - 1)))
                    grid_score -= current_score


    # diagonally
    rows = []
    for i in range(len(grid) - 4):
        row = []
        for j in range(len(grid) - i):
            row.append(grid[j][i + j])
        rows.append(row)
    for i in range(1, len(grid) - 4):
        row = []
        for j in range(len(grid) - i):
            row.append(grid[len(grid) - j - 1][len(grid) - (i + j) - 1])
        rows.append(row)
    for i in range(len(grid) - 4):
        row = []
        for j in range(len(grid) - i):
            row.append(grid[j][len(grid) - (i + j) - 1])
        rows.append(row)
    for i in range(1, len(grid) - 4):
        row = []
        for j in range(len(grid) - i):
            row.append(grid[len(grid) - j - 1][i + j])
        rows.append(row)
    for row in rows:
        for i in range(len(row) - 4):
            cells = row[i:i + 5]

            if -player not in cells:
                if player in cells:
                    index = 0
                    max_sc = 0
                    cur_score = 0
                    while index < 5:
                        if cells[index] == player:
                            cur_score += 1
                        else:
                            if cur_score > max_sc:
                                max_sc = cur_score
                            cur_score = 0
                        index += 1
                    if cur_score > max_sc:
                        max_sc = cur_score
                    current_score =  10 ** (cells.count(player) - 1) + (max_sc * (10 ** (cells.count(player) - 1)))
                    grid_score += current_score
            if player not in cells:
                if -player in cells:
                    index = 0
                    max_sc = 0
                    cur_score = 0
                    while index < 5:
                        if cells[index] == -player:
                            cur_score += 1
                        else:
                            if cur_score > max_sc:
                                max_sc = cur_score
                            cur_score = 0
                        index += 1
                    if cur_score > max_sc:
                        max_sc = cur_score
                    current_score =  10 ** (cells.count(-player) - 1) + (max_sc * (10 ** (cells.count(-player) - 1)))
                    grid_score -= current_score

    return grid_score


def is_game_over(grid, player):
    is_over_vertically = False
    is_over_horizontally = False
    is_over_diagonally = False

    # horizontally
    for i in range(len(grid)):
        for j in range(len(grid) - 4):
            cells = grid[i][j:j + 5]

            if -player not in cells:
                if player in cells:
                    if cells.count(player) == 5:
                        is_over_horizontally = True

    # vertically
    grid = [list(row) for row in zip(*grid)]
    for i in range(len(grid)):
        for j in range(len(grid) - 4):
            cells = grid[i][j:j + 5]

            if -player not in cells:
                if player in cells:
                    if cells.count(player) == 5:
                        is_over_vertically = True
    # diagonally
    rows = []
    for i in range(len(grid) - 4):
        row = []
        for j in range(len(grid) - i):
            row.append(grid[j][i + j])
        rows.append(row)
    for i in range(1, len(grid) - 4):
        row = []
        for j in range(len(grid) - i):
            row.append(grid[len(grid) - j - 1][len(grid) - (i + j) - 1])
        rows.append(row)
    for i in range(len(grid) - 4):
        row = []
        for j in range(len(grid) - i):
            row.append(grid[j][len(grid) - (i + j) - 1])
        rows.append(row)
    for i in range(1, len(grid) - 4):
        row = []
        for j in range(len(grid) - i):
            row.append(grid[len(grid) - j - 1][i + j])
        rows.append(row)
    for row in rows:
        for i in range(len(row) - 4):
            cells = row[i:i + 5]

            if -player not in cells:
                if player in cells:
                    if cells.count(player) == 5:
                        is_over_diagonally = True
    return is_over_diagonally or is_over_horizontally or is_over_vertically


def compute_possible_moves(grid):
    moves_list = []

    for i in range((len(grid))):
        for j in range((len(grid))):

            if grid[i][j] != 0:
                x, y = max(i - 4, 0), max(j - 4, 0)

                for x_raw in range(x, min(i + 5, len(grid))):
                    for y_raw in range(y, min(j + 5, len(grid))):
                        if grid[x_raw][y_raw] == 0:
                            moves_list.append((x_raw, y_raw))                            

    return list(set(moves_list))


# print(compute_possible_moves(grid))
print(compute_best_move(grid, 1))

# print(estimation(grid, 1))
