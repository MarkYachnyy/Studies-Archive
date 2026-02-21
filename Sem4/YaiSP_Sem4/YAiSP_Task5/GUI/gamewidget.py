import time

from PyQt5.QtCore import Qt, QPointF, QThread, QPoint
from PyQt5.QtGui import QPainter, QPen, QBrush, QColor, QPolygonF
from PyQt5.QtWidgets import QWidget

from logic.game import Game


class GameWidget(QWidget):

    def __init__(self, level_id, on_game_over):
        super().__init__()
        self.on_game_over = on_game_over
        self.level_id = level_id
        self.row_spawn_delay = 10
        self.process_blocks_thread = None
        self.player_fall_thread = None
        self.allow_player_controls = False

        self.CELL_SIZE = 70
        self.COLORS = [QColor(*map(int, line.split())) for line in open('../colors.txt')]


    def paintEvent(self, event):
        try:
            painter = QPainter(self)
            if self.game:
                for i in range(self.game.get_row_count):
                    for j in range(self.game.get_col_count):
                        self.paint_cell(painter, self.game.get_cell_value(i, j), j * self.CELL_SIZE, i * self.CELL_SIZE)
        except Exception as e:
            print(e)

    def paint_cell(self, painter: QPainter, color: int, x: int, y: int):
        if color == 0:
            painter.setPen(QPen(Qt.black, 2, Qt.SolidLine))
            painter.setBrush(QBrush(QColor(170, 240, 209), Qt.SolidPattern))
            painter.drawRect(x, y, self.CELL_SIZE, self.CELL_SIZE)
        elif color > 0:
            painter.setPen(QPen(Qt.black, 2, Qt.SolidLine))
            painter.setBrush(QBrush(QColor(102, 51, 0), Qt.SolidPattern))
            painter.drawRect(x, y, self.CELL_SIZE, self.CELL_SIZE)
            painter.setPen(QPen(Qt.lightGray, 4, Qt.SolidLine))
            painter.setBrush(QBrush(self.COLORS[color - 1], Qt.SolidPattern))
            padding = 10
            painter.drawRect(x + padding, y + padding, self.CELL_SIZE - padding * 2, self.CELL_SIZE - padding * 2)
        else:
            painter.setPen(QPen(Qt.black, 2, Qt.SolidLine))
            painter.setBrush(QBrush(QColor(102, 51, 0), Qt.SolidPattern))
            painter.drawRect(x, y, self.CELL_SIZE, self.CELL_SIZE)
            painter.setBrush(QBrush(Qt.red, Qt.SolidPattern))
            polygon = QPolygonF()
            points = [(0, self.CELL_SIZE / 2), (self.CELL_SIZE / 2, self.CELL_SIZE),
                      (self.CELL_SIZE, self.CELL_SIZE / 2), (self.CELL_SIZE / 2, 0)]
            for point in points:
                polygon.append(QPointF(point[0] + x, point[1] + y))
            painter.drawPolygon(polygon)

            painter.setPen(QPen(Qt.lightGray, 4, Qt.SolidLine))
            painter.setBrush(QBrush(Qt.white, Qt.SolidPattern))
            painter.drawEllipse(QPoint(x + self.CELL_SIZE // 2, y + self.CELL_SIZE // 2), self.CELL_SIZE / 4,
                                self.CELL_SIZE / 4)

    def new_game(self):
        level = self.parse_level(f"../levels/level_0{self.level_id}.txt")
        self.player_fall_thread = None
        self.process_blocks_thread = None
        self.game = Game(*level)
        self.row_spawn_delay = self.game.row_spawn_delay
        self.fit_window_size()
        self.allow_player_controls = True
        self.spawn_row_timer = self.RowSpawnThread(self)
        self.spawn_row_timer.start()

    def fit_window_size(self):
        if self.game:
            width = self.game.get_col_count * self.CELL_SIZE
            height = self.game.get_row_count * self.CELL_SIZE
            self.setFixedSize(width, height)

    @staticmethod
    def parse_level(filename):
        file = open(filename)
        lines = [_ for _ in file]
        max_color, row_spawn_delay = map(int, lines[0].split())
        field = [[int(a) for a in line.split()] for line in lines[1:]]
        return field, max_color, row_spawn_delay

    def keyPressEvent(self, event):
        key = event.key()
        if self.allow_player_controls:
            if key == Qt.Key_Up:
                self.game.up_arrow_pressed()
                self.process_blocks()
                self.drop_player()
            elif key == Qt.Key_Down:
                self.game.down_arrow_pressed()
                self.process_blocks()
            elif key == Qt.Key_Left:
                if self.game.left_arrow_pressed():
                    self.process_blocks()
                    self.drop_player()
                else:
                    self.process_blocks()
            elif key == Qt.Key_Right:
                if self.game.right_arrow_pressed():
                    self.process_blocks()
                    self.drop_player()
                else:
                    self.process_blocks()
            self.update()

    def process_blocks(self):
        if self.process_blocks_thread is None:
            self.process_blocks_thread = self.ProcessBlocksThread(self)
            self.process_blocks_thread.start()
        else:
            if not self.process_blocks_thread.is_alive():
                self.process_blocks_thread = self.ProcessBlocksThread(self)
                self.process_blocks_thread.start()
                return

    class ProcessBlocksThread(QThread):

        def __init__(self, game_widget):
            super().__init__()
            self._alive = True
            self.game_widget = game_widget

        def run(self) -> None:
            self._alive = True
            self.game_widget.destroy_lines()
            self.game_widget.update()
            time.sleep(0.05)
            while self.game_widget.game.drop_blocks():
                self.game_widget.update()
                time.sleep(0.05)
            while self.game_widget.destroy_lines():
                self.game_widget.update()
                time.sleep(0.05)
                while self.game_widget.game.drop_blocks():
                    self.game_widget.update()
                    time.sleep(0.05)
            self._alive = False

        def is_alive(self):
            return self._alive

    def drop_player(self):
        if self.player_fall_thread is None or (not self.player_fall_thread.is_alive()):
            self.player_fall_thread = self.DropPlayerThread(self)  # Thread(target=self.drop_player_function)
            self.player_fall_thread.start()

    class DropPlayerThread(QThread):
        def __init__(self, game_widget):
            super().__init__()
            self._alive = True
            self.game_widget = game_widget

        def run(self) -> None:
            self._alive = True
            self.game_widget.allow_player_controls = False
            self.game_widget.update()
            time.sleep(0.05)
            while self.game_widget.game.drop_player():
                self.game_widget.update()
                time.sleep(0.05)
            self.game_widget.allow_player_controls = True
            self._alive = False

        def is_alive(self):
            return self._alive

    def destroy_lines(self):
        if self.game.destroy_lines():
            if self.game.can_player_fall():
                self.game.drop_player()
            return True
        return False

    class RowSpawnThread(QThread):
        def __init__(self, game_widget):
            super().__init__()
            self._alive = True
            self.game_widget = game_widget
            self._is_running = True

        def run(self) -> None:
            while self._is_running:
                self.sleep(self.game_widget.row_spawn_delay)
                if self._is_running:
                    if self.game_widget.game.spawn_new_row():
                        self.game_widget.process_blocks()
                    else:
                        self.game_widget.stop_game()

        def stop(self):
            self._is_running = False

    def stop_game(self):
        self.allow_player_controls = False
        self.on_game_over()
        self.spawn_row_timer.stop()
