import time

from PyQt5 import QtCore
from PyQt5.QtCore import QThread
from PyQt5.QtGui import QIcon
from PyQt5.QtWidgets import QMainWindow, QWidget, QVBoxLayout, QLabel, QPushButton

from GUI.gamewidget import GameWidget


class GameWindow(QMainWindow):
    def __init__(self, level_id, main_window):
        super().__init__()
        self.main_window = main_window
        self.level_id = level_id
        self.time_timer = None
        self.time_left = 100
        self.setWindowIcon(QIcon("../images/icon.jpg"))


        self.central_widget = QWidget()
        self.layout = QVBoxLayout()
        self.layout.setAlignment(QtCore.Qt.AlignCenter)

        self.label = QLabel()
        self.layout.addWidget(self.label)
        self.label.setAlignment(QtCore.Qt.AlignCenter)

        self.game_widget = GameWidget(self.level_id, self.game_lose)
        self.layout.addWidget(self.game_widget)
        self.setCentralWidget(self.central_widget)

        self.button_back_to_menu = QPushButton("Назад в меню")
        self.button_back_to_menu.setStyleSheet("QPushButton{color: white;"
                                               "background: red;"
                                               "border: 2px solid black;"
                                               "font-size:20px;"
                                               "border-radius: 10px;"
                                               "padding:5px;"
                                               "margin:5px}"
                                               "QPushButton::hover{"
                                               "background: white"
                                               "color:red"
                                               "}")

        self.layout.addWidget(self.button_back_to_menu)
        self.button_back_to_menu.clicked.connect(self.back_to_menu)

        self.button_restart = QPushButton("Заново")
        self.button_restart.setStyleSheet("QPushButton{color: white;"
                                          "background:orange;"
                                          "border: 2px solid black;"
                                          "font-size:20px;"
                                          "border-radius: 10px;"
                                          "padding:5px;"
                                          "margin:5px}"
                                          "QPushButton::hover{"
                                          "background: white"
                                          "color:orange"
                                          "}")
        self.button_restart.clicked.connect(self.restart)
        self.layout.addWidget(self.button_restart)

        self.new_game()
        self.central_widget.setLayout(self.layout)
        self.setWindowTitle("Cubicon")

    def new_game(self):
        self.setFocus()
        self.time_left = 100
        self.label.setStyleSheet("font-size:30px;"
                                 "font-family:Comic Sans MS;"
                                 "color:black;")
        self.label.setText(f"Осталось времени: {self.time_left}")
        self.time_timer = self.TimeTimer(self)
        self.time_timer.start()
        self.game_widget.new_game()

    def game_lose(self):
        self.time_timer.stop()
        self.label.setStyleSheet("font-size:30px;"
                                 "font-family:Comic Sans MS;"
                                 "color:red;")
        self.label.setText("Игра окончена!")

    def game_win(self):
        self.game_widget.stop_game()
        self.label.setStyleSheet("font-size:30px;"
                                 "font-family:Comic Sans MS;"
                                 "color:green;")
        self.label.setText("Уровень пройден!")

    def keyPressEvent(self, event):
        self.game_widget.keyPressEvent(event)

    def back_to_menu(self):
        self.main_window.show()
        self.game_widget.stop_game()
        self.close()

    def restart(self):
        self.game_widget.stop_game()
        time.sleep(0.5)
        self.new_game()
        self.game_widget.update()

    class TimeTimer(QThread):
        def __init__(self, game_window):
            super().__init__()
            self.game_window = game_window
            self._running = True

        def run(self) -> None:
            while self.game_window.time_left > 0:
                time.sleep(1)
                if not self._running:
                    return
                self.game_window.time_left -= 1
                self.game_window.label.setText(f"Осталось времени: {self.game_window.time_left}")
            self.game_window.game_win()

        def stop(self):
            self._running = False
