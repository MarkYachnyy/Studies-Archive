import math


def findNextCellToFill(grid, i, j):
    bs = int(round(math.sqrt(len(grid))))
    for x in range(i, bs ** 2):
        for y in range(j, bs ** 2):
            if grid[x][y] == 0:
                return x, y
    for x in range(0, bs ** 2):
        for y in range(0, bs ** 2):
            if grid[x][y] == 0:
                return x, y
    return -1, -1


def isValid(grid, i, j, e):
    bs = int(round(math.sqrt(len(grid))))
    rowOk = all([e != grid[i][x] for x in range(bs ** 2)])
    if rowOk:
        columnOk = all([e != grid[x][j] for x in range(bs ** 2)])
        if columnOk:
            # finding the top left x,y co-ordinates of the section containing the i,j cell
            secTopX, secTopY = bs * (i // bs), bs * (j // bs)  # floored quotient should be used here.
            for x in range(secTopX, secTopX + bs):
                for y in range(secTopY, secTopY + bs):
                    if grid[x][y] == e:
                        return False
            return True
    return False


def solveSudoku(grid, i=0, j=0):
    i, j = findNextCellToFill(grid, i, j)
    if i == -1:
        return True
    for e in range(1, len(grid) + 1):
        if isValid(grid, i, j, e):
            grid[i][j] = e
            if solveSudoku(grid, i, j):
                return True
            # Undo the current cell for backtracking
            grid[i][j] = 0
    return False