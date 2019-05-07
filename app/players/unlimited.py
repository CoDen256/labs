from pprint import pprint


grid = [[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
]


def compute_best_move(grid, depth=2):
    if depth == 0:
        # do estimation

        return None, mark

    else:
        possible_moves = compute_possible_moves(grid)

        best_move, best_mark = None, -1e9

        for current_move in possible_moves:
            current_grid = copy(grid)
            grid.move(current_move)

            _, current_mark = compute_best_move(grid, depth=depth-1)
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
                    grid_score += 10 ** (cells.count(player) - 1)

    # vertically
    grid = [list(row) for row in zip(*grid)]
    for i in range(len(grid)):
        for j in range(len(grid) - 4):
            cells = grid[i][j:j + 5]

            if -player not in cells:
                if player in cells:
                    grid_score += 10 ** (cells.count(player) - 1)

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
                    grid_score += 10 ** (cells.count(player) - 1)

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

    
print(estimation(grid, 1))
