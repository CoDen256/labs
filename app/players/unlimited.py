grid = [[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0],
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
        for j in range(len(grid[0]) - 4):
            cells = grid[i][j:j+5]

            if not cells.count(-player):
                if cells.count(player):
                    grid_score += 10 ** (cells.count(player) - 1)

    # vertically
    grid = list(zip(grid))
    for i in range(len(grid)):
        for j in range(len(grid[0]) - 4):
            cells = grid[i][j:j+5]

            if not cells.count(-player):
                if cells.count(player):
                    grid_score += 10 ** (cells.count(player) - 1)

    return grid_score


print(estimation(grid, 1))
    