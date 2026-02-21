import math
import sys
import time
from tkinter import Tk
from tkinter.filedialog import askopenfilename

import cv2
from PyQt5.QtCore import QThread, QRectF, Qt
from PyQt5.QtGui import QPainter, QImage, QBrush, QPen
from PyQt5.QtWidgets import QMainWindow, QWidget, QVBoxLayout, QApplication, QHBoxLayout, QPushButton, QLabel, \
    QMessageBox

from gaze_tracking import GazeTracking


class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()

        self.central_widget = QWidget()
        self.layout = QVBoxLayout()
        self.layout.setAlignment(Qt.AlignCenter)
        self.central_widget.setLayout(self.layout)
        self.setCentralWidget(self.central_widget)
        self.central_widget.setStyleSheet("QPushButton{"
                                          "font-size:20px;"
                                          "color:black;"
                                          "border:3px solid black;"
                                          "border-radius:10px;"
                                          "background:white;"
                                          "padding:5px;"
                                          "margin:0px 5px;"
                                          "}"
                                          "QPushButton::hover{"
                                          "color:white;"
                                          "background:black;"
                                          "}")

        def update_speed1(s):
            self.game_widget.speed1 = s

        def update_speed2(s):
            self.game_widget.speed2 = s

        cw1 = EyeTrackerWidget(0, update_speed1)
        cw2 = EyeTrackerWidget(1, update_speed2)

        def win(num):
            cw1.self_setting_now(True)
            cw2.self_setting_now(True)
            if num == 1:
                self.label_status.setText("Игрок 1 победил!")
            else:
                self.label_status.setText("Игрок 2 победил!")

        hlayout = QHBoxLayout()
        self.game_widget = GameWidget(win)

        self.layout.addWidget(cw1)
        cw1.setFixedSize(400, 300)
        hlayout.addWidget(cw1)

        self.calibrate_button = QPushButton("Откалибровать\nи начать")
        vlayout = QVBoxLayout()
        vlayout.setAlignment(Qt.AlignCenter)
        vlayout.addWidget(self.calibrate_button)

        self.restart_button = QPushButton("Заново")
        vlayout.addWidget(self.restart_button)

        hlayout.addLayout(vlayout)

        self.layout.addWidget(cw2)
        cw2.setFixedSize(400, 300)
        hlayout.addWidget(cw2)

        self.layout.addLayout(hlayout)
        self.layout.addWidget(self.game_widget)

        def start_game():
            cw1.self_setting_now(False)
            cw2.self_setting_now(False)
            self.game_widget.setting_now = False
            self.label_status.setText("Вперёд!")

        self.calibrate_button.clicked.connect(start_game)

        def restart():
            self.game_widget.player1_coords = [self.game_widget.CELL_SIZE / 4, self.game_widget.CELL_SIZE / 2]
            self.game_widget.player2_coords = [self.game_widget.CELL_SIZE * 3 / 4, self.game_widget.CELL_SIZE / 2]
            self.game_widget.setting_now = True
            cw1.self_setting_now(True)
            cw2.self_setting_now(True)
            self.label_status.setText('Приблизьте лица к камерам,\nзатем нажмите "Откалибровать и начать"')
            self.game_widget.speed1 = [0, 0]
            self.game_widget.speed2 = [0, 0]

        def open_file():
            root = Tk()
            root.withdraw()
            root.update()
            pathString = askopenfilename(filetypes=[("Text files", "*.txt")], initialdir='.')
            if pathString:
                try:
                    openFile = open(pathString, 'r')
                    intervals = [[int(a) for a in line.split()] for line in [_ for _ in openFile]]
                    if len(intervals) == 0:
                        raise ValueError()
                    self.game_widget.field = intervals
                    self.game_widget.update()
                except FileNotFoundError:
                    print("File not found")
                except ValueError:
                    msg = QMessageBox()
                    msg.setIcon(QMessageBox.Critical)
                    msg.setText("Неверный формат содержания файла")
                    msg.setWindowTitle("Ошибка")
                    msg.exec_()
            root.destroy()

        self.open_file_btn = QPushButton("Взять уровень из файла")
        self.open_file_btn.clicked.connect(open_file)
        vlayout.addWidget(self.open_file_btn)
        self.restart_button.clicked.connect(restart)
        self.label_status = QLabel('Приблизьте лица к камерам,\nзатем нажмите "Откалибровать и начать"')
        self.label_status.setAlignment(Qt.AlignCenter)
        vlayout.addWidget(self.label_status)


class GameWidget(QWidget):
    def __init__(self, on_game_win):
        super().__init__()
        self.on_game_win = on_game_win
        self.CELL_SIZE = 50
        self.field = [[1 for __ in range(20)] for _ in range(10)]
        self.field[0][0] = 0
        self.field[-1][-1] = 0
        self.setFixedSize(self.CELL_SIZE * len(self.field[0]), self.CELL_SIZE * len(self.field))
        self.speed1 = [0, 0]
        self.speed2 = [0, 0]
        self.player1_coords = [self.CELL_SIZE / 4, self.CELL_SIZE / 2]
        self.player2_coords = [self.CELL_SIZE * 3 / 4, self.CELL_SIZE / 2]
        self.thread = self.MyThread(self)
        self.setting_now = True
        self.thread.start()

    def paintEvent(self, event):
        painter = QPainter(self)
        for i in range(len(self.field)):
            for j in range(len(self.field[0])):
                painter.setPen(QPen(Qt.lightGray, 1, Qt.SolidLine))
                if i == 0 and j == 0:
                    color = Qt.red
                elif i == len(self.field) - 1 and j == len(self.field[0]) - 1:
                    color = Qt.green
                elif self.field[i][j] == 0:
                    color = Qt.white
                else:
                    color = Qt.black
                painter.setBrush(QBrush(color, Qt.SolidPattern))
                painter.drawRect(self.CELL_SIZE * j, self.CELL_SIZE * i, self.CELL_SIZE, self.CELL_SIZE)

        j = int(self.player1_coords[0] // self.CELL_SIZE)
        i = int(self.player1_coords[1] // self.CELL_SIZE)

        if i == len(self.field) - 1 and j == len(self.field[0]) - 1:
            self.on_game_win(1)
            self.setting_now = True
            # self.player1_coords = [self.CELL_SIZE / 4, self.CELL_SIZE / 2]
            # self.player2_coords = [self.CELL_SIZE * 3 / 4, self.CELL_SIZE / 2]
            self.speed1 = [0, 0]
            self.speed2 = [0, 0]

        j = int(self.player2_coords[0] // self.CELL_SIZE)
        i = int(self.player2_coords[1] // self.CELL_SIZE)

        if i == len(self.field) - 1 and j == len(self.field[0]) - 1:
            self.on_game_win(2)
            self.setting_now = True
            # self.player1_coords = [self.CELL_SIZE / 4, self.CELL_SIZE / 2]
            # self.player2_coords = [self.CELL_SIZE * 3 / 4, self.CELL_SIZE / 2]
            self.speed1 = [0, 0]
            self.speed2 = [0, 0]

        self.setFixedSize(self.CELL_SIZE * len(self.field[0]), self.CELL_SIZE * len(self.field))

        if self.speed1[0] != 0:
            angle1 = math.atan(self.speed1[1] / self.speed1[0]) + (math.pi if self.speed1[0] > 0 else 0)
        else:
            angle1 = math.pi / 2 if self.speed1[1] < 0 else -math.pi / 2
        angle1 /= math.pi
        angle1 *= 180
        painter.translate(*self.player1_coords)
        painter.rotate(angle1)
        painter.drawImage(QRectF(-self.CELL_SIZE / 2, -self.CELL_SIZE / 4, self.CELL_SIZE, self.CELL_SIZE / 2),
                          QImage('car_red.png'))
        painter.rotate(-angle1)
        painter.translate(-self.player1_coords[0], -self.player1_coords[1])

        if self.speed2[0] != 0:
            angle2 = math.atan(self.speed2[1] / self.speed2[0]) + (math.pi if self.speed2[0] > 0 else 0)
        else:
            angle2 = math.pi / 2 if self.speed2[1] < 0 else -math.pi / 2
        angle2 /= math.pi
        angle2 *= 180
        angle2 += 180
        painter.translate(*self.player2_coords)
        painter.rotate(angle2)
        painter.drawImage(QRectF(-self.CELL_SIZE / 2, -self.CELL_SIZE / 4, self.CELL_SIZE, self.CELL_SIZE / 2),
                          QImage('car_blue.png'))
        painter.rotate(-angle2)
        painter.translate(-self.player2_coords[0], -self.player2_coords[1])

    def mousePressEvent(self, event):
        j = event.x() // self.CELL_SIZE
        i = event.y() // self.CELL_SIZE
        if not (i == 0 and j == 0 or i == len(self.field) and j == len(self.field[0])) and self.setting_now:
            self.field[i][j] = 1 - self.field[i][j]
        self.update()

    class MyThread(QThread):
        def __init__(self, game_widget):
            super().__init__()
            self.game_widget = game_widget

        def run(self):
            while True:
                time.sleep(0.1)
                steps_ahead = 6
                if not self.game_widget.setting_now:
                    x_ahead = self.game_widget.player1_coords[0] + self.game_widget.speed1[0] * steps_ahead
                    y_ahead = self.game_widget.player1_coords[1] + self.game_widget.speed1[1] * steps_ahead
                    j = int(x_ahead // self.game_widget.CELL_SIZE)
                    i = int(y_ahead // self.game_widget.CELL_SIZE)
                    if not (j < 0 or i < 0 or i >= len(self.game_widget.field) or j >= len(self.game_widget.field[0]) or
                            self.game_widget.field[i][j] == 1):
                        self.game_widget.player1_coords[0] += self.game_widget.speed1[0] * 4
                        self.game_widget.player1_coords[1] += self.game_widget.speed1[1] * 4

                    x_ahead = self.game_widget.player2_coords[0] + self.game_widget.speed2[0] * steps_ahead
                    y_ahead = self.game_widget.player2_coords[1] + self.game_widget.speed2[1] * steps_ahead
                    j = int(x_ahead // self.game_widget.CELL_SIZE)
                    i = int(y_ahead // self.game_widget.CELL_SIZE)
                    if not (j < 0 or i < 0 or i >= len(self.game_widget.field) or j >= len(self.game_widget.field[0]) or
                            self.game_widget.field[i][j] == 1):
                        self.game_widget.player2_coords[0] += self.game_widget.speed2[0] * 4
                        self.game_widget.player2_coords[1] += self.game_widget.speed2[1] * 4
                self.game_widget.update()


class EyeTrackerWidget(QWidget):
    def __init__(self, camera_num, update_speed):
        super().__init__()
        self.gaze = GazeTracking()
        self.cam = cv2.VideoCapture(camera_num)
        self.image = None
        self.thread = self.MyThread(self)
        self.update_speed = update_speed
        self.thread.start()
        self.setting_now = True

    def paintEvent(self, event):
        painter = QPainter(self)
        if self.image is not None:
            painter.drawImage(QRectF(0, 0, self.width(), self.height()), self.image)
            painter.drawImage(QRectF(0, 0, self.width(), self.height() / 6), QImage('live.jpg'))
            painter.drawImage(QRectF(self.width() / 4, 0, self.width() / 4, self.height() / 6), self.image)

    def set_image(self, frame):
        frame1 = frame
        self.image = QImage(frame1.data, frame1.shape[1], frame1.shape[0], QImage.Format_BGR888)

    def self_setting_now(self, value):
        self.setting_now = value

    class MyThread(QThread):
        def __init__(self, etw):
            super().__init__()
            self.eye_tracking_widget = etw

        def run(self):
            while True:
                try:
                    _, frame = self.eye_tracking_widget.cam.read()
                    self.eye_tracking_widget.gaze.refresh(frame)
                    vent = self.eye_tracking_widget.gaze.annotated_frame()
                except Exception:
                    continue
                self.eye_tracking_widget.set_image(vent)
                self.eye_tracking_widget.update()
                if self.eye_tracking_widget.setting_now:
                    self.eye_tracking_widget.initial_coords = self.eye_tracking_widget.gaze.pupil_left_coords()
                else:
                    pupil_coords = self.eye_tracking_widget.gaze.pupil_left_coords()
                    if pupil_coords is not None and self.eye_tracking_widget.initial_coords is not None:
                        speed = [pupil_coords[0] - self.eye_tracking_widget.initial_coords[0],
                                 pupil_coords[1] - self.eye_tracking_widget.initial_coords[1]]
                        length = math.sqrt(speed[0] ** 2 + speed[1] ** 2)
                        if length > 1:
                            speed[0] /= -length
                            speed[1] /= length
                        else:
                            speed = (0, 0)

                        self.eye_tracking_widget.update_speed(speed)


def main():
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    sys.exit(app.exec_())


if __name__ == '__main__':
    main()
