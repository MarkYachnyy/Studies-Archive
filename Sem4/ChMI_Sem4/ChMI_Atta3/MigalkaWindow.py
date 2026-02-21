import random
import time

from PyQt5.QtCore import QThread
from PyQt5.QtWidgets import QMainWindow, QLabel, QWidget, QVBoxLayout

class MigalkaWindow(QMainWindow):
    def __init__(self, main_window):
        super().__init__()
        self.main_window = main_window
        self.label = QLabel()
        self.label.setFixedSize(1500, 900)
        self.label.setStyleSheet("background: black;")
        self.thread = self.FlashThread(self)
        cw = QWidget()
        lyt = QVBoxLayout()
        lyt.addWidget(self.label)
        cw.setLayout(lyt)
        self.setCentralWidget(cw)
        self.thread.start()

    def set_white(self):
        self.label.setStyleSheet("background: white;")
        self.update()

    def set_black(self):
        self.label.setStyleSheet("background: black;")
        self.update()

    class FlashThread(QThread):
        def __init__(self, window):
            super().__init__()
            self.window = window

        def run(self):
            delta = 0.5
            while (True):
                time.sleep(delta)
                self.window.main_window.start_new_tick()
                a = random.randint(0, 100)
                if a >= 15:
                    self.window.set_white()

                time.sleep(0.06)
                self.window.set_black()