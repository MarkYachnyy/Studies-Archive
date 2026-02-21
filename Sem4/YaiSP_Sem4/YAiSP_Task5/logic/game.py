import random as rnd


class Game:
    def __init__(self, field, color_count, row_spawn_delay):
        super().__init__()
        self.field = field
        self.color_count = color_count
        self.cur_row = len(field) - 1
        self.row_spawn_delay = row_spawn_delay
        self.min_destroy_line_len = 3
        self.cur_col = 0

    def left_arrow_pressed(self):
        if self.cur_col == 0:
            return False
        else:
            self.field[self.cur_row][self.cur_col] = self.field[self.cur_row][self.cur_col - 1]
            self.cur_col -= 1
            self.field[self.cur_row][self.cur_col] = -1
            return self.can_player_fall()

    def right_arrow_pressed(self):
        if self.cur_col == len(self.field[0]) - 1:
            return False
        else:
            self.field[self.cur_row][self.cur_col] = self.field[self.cur_row][self.cur_col + 1]
            self.cur_col += 1
            self.field[self.cur_row][self.cur_col] = -1
            return self.can_player_fall()

    def down_arrow_pressed(self):
        if self.cur_row != len(self.field) - 1:
            self.field[self.cur_row][self.cur_col] = self.field[self.cur_row + 1][self.cur_col]
            self.cur_row += 1
            self.field[self.cur_row][self.cur_col] = -1

    def up_arrow_pressed(self):
        if self.cur_row != 0 and self.field[self.cur_row - 1][self.cur_col] != 0:
            self.field[self.cur_row][self.cur_col] = self.field[self.cur_row - 1][self.cur_col]
            self.cur_row -= 1
            self.field[self.cur_row][self.cur_col] = -1

    def spawn_new_row(self):
        for j in range(0, 2):
            if self.field[0][j] > 0:
                return False
            else:
                self.field[0][j] = rnd.randint(1, self.color_count)

        for j in range(2, len(self.field[0])):
            if self.field[0][j] > 0:
                return False
            else:
                self.field[0][j] = rnd.randint(1, self.color_count)
                while self.field[0][j] == self.field[0][j - 1] and self.field[0][j] == self.field[0][j - 2]:
                    self.field[0][j] = rnd.randint(1, self.color_count)
        return True

    def drop_blocks(self):
        res = False
        for i in reversed(range(1, len(self.field))):
            for j in range(0, len(self.field[0])):
                if self.field[i][j] == 0 and self.field[i - 1][j] > 0:
                    res = True
                    self.field[i][j] = self.field[i - 1][j]
                    self.field[i - 1][j] = 0
        return res

    def drop_player(self):
        if self.can_player_fall():
            self.field[self.cur_row][self.cur_col] = 0
            self.cur_row += 1
            self.field[self.cur_row][self.cur_col] = -1
            return True
        else:
            return False

    def can_player_fall(self):
        return False if self.cur_row == len(self.field) - 1 else self.field[self.cur_row + 1][self.cur_col] == 0

    def destroy_lines(self):
        res = False
        for i in range(0, len(self.field)):
            c = 1
            for j in range(1, len(self.field[0])):
                if self.field[i][j] > 0 and self.field[i][j] == self.field[i][j - 1]:
                    c += 1
                else:
                    if c >= self.min_destroy_line_len:
                        res = True
                        for k in reversed(range(j - c, j)):
                            self.field[i][k] = 0
                    c = 1
            if c >= self.min_destroy_line_len:
                res = True
                for k in reversed(range(len(self.field[0]) - c, len(self.field[0]))):
                    self.field[i][k] = 0
        for j in range(len(self.field[0])):
            c = 1
            for i in range(1, len(self.field)):
                if self.field[i][j] > 0 and self.field[i][j] == self.field[i - 1][j]:
                    c += 1
                else:
                    if c >= self.min_destroy_line_len:
                        res = True
                        for k in reversed(range(i - c, i)):
                            self.field[k][j] = 0
                    c = 1
            if c >= self.min_destroy_line_len:
                res = True
                for k in reversed(range(len(self.field) - c, len(self.field))):
                    self.field[k][j] = 0
        return res

    @property
    def get_row_count(self):
        return 0 if not self.field else len(self.field)

    @property
    def get_col_count(self):
        return 0 if not self.field else len(self.field[0])

    def get_cell_value(self, row, col):
        if row < 0 or row >= self.get_row_count or col < 0 or col >= self.get_col_count:
            return 0
        else:
            return self.field[row][col]
