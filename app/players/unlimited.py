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


def score_estimation(number):
    if number == 0:
        return 0
    if number == 1:
        return 1
    if number == 2:
        return 10
    if number == 3:
        return 100
    if number == 4:
        return 1000
    if number == 5:
        return 10000


def estimation(grid, player):
    grid_score = 0
    #horizontally
    for i in range(15):
        for j in range(15-4):

            index = j
            finish_index = j + 5
            max_length_score = 0
            current_score = 0

            while(index < finish_index):
                if grid[i][index] == player:
                    current_score += 1
                    print(f"{i} {j}")
                    print(f"{i} {index}")
                elif grid[i][index] == player * -1:
                    max_length_score = 0
                    break
                else:
                    if current_score > max_length_score:
                        max_length_score = current_score
                        current_score = 0
                if current_score > max_length_score:
                    max_length_score = current_score
                
                index += 1
            
            grid_score += score_estimation(max_length_score)
            if score_estimation(max_length_score) > 0:
                print(score_estimation(max_length_score))

    return grid_score


print(estimation(grid, 1))
            





    



    