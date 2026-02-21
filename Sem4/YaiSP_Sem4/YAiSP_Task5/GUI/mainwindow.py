from PIL import Image
from PyQt5 import QtCore
from PyQt5.QtCore import QSize
from PyQt5.QtGui import QIcon, QImage
from PyQt5.QtWidgets import QMainWindow, QVBoxLayout, QLabel, QWidget, QPushButton, QHBoxLayout

from GUI.dialogs import DialogHowToPlay, DialogAboutDeveloper, DialogColorSettings
from GUI.gamewindow import GameWindow


class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        bg_img = QImage("../images/main_bg.png")
        self.setFixedSize(bg_img.width(), bg_img.height())
        self.central_widget = QWidget()
        self.setWindowTitle("Cubicon")
        self.setWindowIcon(QIcon("../images/icon.jpg"))
        self.layout = QVBoxLayout()
        self.central_widget.setLayout(self.layout)
        self.central_widget.setObjectName("central_widget")
        self.central_widget.setStyleSheet("QWidget#central_widget{background: url(../images/main_bg.png);}")
        self.layout.setAlignment(QtCore.Qt.AlignCenter)
        self.setCentralWidget(self.central_widget)

        self.set_main_layout()

    def set_main_layout(self):
        lt = QVBoxLayout()
        self.label_name = QLabel("Cubicon")
        self.label_name.setStyleSheet("color:blue;"
                                      "font-size:60px;"
                                      "font-family:Comic Sans MS;"
                                      "background:white;"
                                      "border: 4px solid black;"
                                      "padding:10px;"
                                      "margin:10px;")
        lt.addWidget(self.label_name)

        lt.setAlignment(QtCore.Qt.AlignCenter)

        self.button_play = QPushButton("Играть")
        self.button_play.setStyleSheet("QPushButton{"
                                       "color:white;"
                                       "font-size:50px;"
                                       "font-family:Comic Sans MS;"
                                       "background:red;"
                                       "border: 6px solid black;"
                                       "padding:10px;"
                                       "border-radius:10px;"
                                       "margin:10px;}"
                                       "QPushButton::hover{"
                                       "color:red;"
                                       "background:white;"
                                       "}")
        lt.addWidget(self.button_play)
        self.button_play.clicked.connect(self.set_levels_layout)

        self.button_about_developer = QPushButton("Об авторе")
        self.button_about_developer.setStyleSheet("QPushButton{"
                                                  "color:white;"
                                                  "font-size:40px;"
                                                  "font-family:Comic Sans MS;"
                                                  "background:orange;"
                                                  "border: 6px solid black;"
                                                  "padding:10px;"
                                                  "border-radius:10px;"
                                                  "margin:10px;}"
                                                  "QPushButton::hover{"
                                                  "color:orange;"
                                                  "background:white;"
                                                  "}")
        self.button_about_developer.clicked.connect(lambda: DialogAboutDeveloper().exec_())
        lt.addWidget(self.button_about_developer)

        self.button_how_to_play = QPushButton("Как играть")
        self.button_how_to_play.setStyleSheet("QPushButton{"
                                              "color:white;"
                                              "font-size:40px;"
                                              "font-family:Comic Sans MS;"
                                              "background:green;"
                                              "border: 6px solid black;"
                                              "padding:10px;"
                                              "border-radius:10px;"
                                              "margin:10px;}"
                                              "QPushButton::hover{"
                                              "color:green;"
                                              "background:white;"
                                              "}")
        self.button_how_to_play.clicked.connect(lambda: DialogHowToPlay().exec_())
        lt.addWidget(self.button_how_to_play)

        self.button_settings = QPushButton("Настройки")
        self.button_settings.setStyleSheet("QPushButton{"
                                           "color:white;"
                                           "font-size:40px;"
                                           "font-family:Comic Sans MS;"
                                           "background:blue;"
                                           "border: 6px solid black;"
                                           "padding:10px;"
                                           "border-radius:10px;"
                                           "margin:10px;}"
                                           "QPushButton::hover{"
                                           "color:blue;"
                                           "background:white;"
                                           "}")
        self.button_settings.clicked.connect(self.set_settings_layout)
        lt.addWidget(self.button_settings)

        self.clean_central_layout()
        self.layout.addLayout(lt)

    def set_levels_layout(self):
        lt = QVBoxLayout()
        self.label_name = QLabel("Cubicon")
        self.label_name.setStyleSheet("color:blue;"
                                      "font-size:60px;"
                                      "font-family:Comic Sans MS;"
                                      "background:white;"
                                      "border: 4px solid black;"
                                      "padding:10px;"
                                      "margin:10px;")
        lt.addWidget(self.label_name)

        for i in range(3):
            btn = QPushButton(f"Уровень {i + 1}")
            btn.setStyleSheet("QPushButton{"
                              "color:white;"
                              "font-size:40px;"
                              "font-family:Comic Sans MS;"
                              "background:green;"
                              "border: 6px solid black;"
                              "padding:10px;"
                              "border-radius:10px;"
                              "margin:10px;}"
                              "QPushButton::hover{"
                              "color:green;"
                              "background:white;"
                              "}")
            btn.clicked.connect(self.start_game_gener(i + 1))
            lt.addWidget(btn)
        return_btn = QPushButton("Назад")
        return_btn.setStyleSheet("QPushButton{"
                                 "color:white;"
                                 "font-size:40px;"
                                 "font-family:Comic Sans MS;"
                                 "background:red;"
                                 "border: 6px solid black;"
                                 "padding:10px;"
                                 "border-radius:10px;"
                                 "margin:10px;}"
                                 "QPushButton::hover{"
                                 "color:red;"
                                 "background:white;"
                                 "}")
        return_btn.clicked.connect(self.set_main_layout)
        lt.addWidget(return_btn)
        self.clean_central_layout()
        self.layout.addLayout(lt)

    def set_settings_layout(self):
        lt = QVBoxLayout()
        self.label_name = QLabel("Настройки")
        self.label_name.setStyleSheet("color:blue;"
                                      "font-size:40px;"
                                      "font-family:Comic Sans MS;"
                                      "background:white;"
                                      "border: 4px solid black;"
                                      "padding:10px;"
                                      "margin:10px;")
        lt.addWidget(self.label_name)

        color_settings_btn = QPushButton("Настроить\nцвета")
        color_settings_btn.setStyleSheet("QPushButton{"
                                         "color:white;"
                                         "font-size:40px;"
                                         "font-family:Comic Sans MS;"
                                         "background:orange;"
                                         "border: 6px solid black;"
                                         "padding:10px;"
                                         "border-radius:10px;"
                                         "margin:10px;}"
                                         "QPushButton::hover{"
                                         "color:red;"
                                         "background:white;"
                                         "}")
        color_settings_btn.clicked.connect(lambda: DialogColorSettings().exec_())
        lt.addWidget(color_settings_btn)

        return_btn = QPushButton("Назад")
        return_btn.setStyleSheet("QPushButton{"
                                 "color:white;"
                                 "font-size:40px;"
                                 "font-family:Comic Sans MS;"
                                 "background:red;"
                                 "border: 6px solid black;"
                                 "padding:10px;"
                                 "border-radius:10px;"
                                 "margin:10px;}"
                                 "QPushButton::hover{"
                                 "color:red;"
                                 "background:white;"
                                 "}")
        return_btn.clicked.connect(self.set_main_layout)
        lt.addWidget(return_btn)
        self.clean_central_layout()
        self.layout.addLayout(lt)

    def recursive_clean(self, layout):
        for i in reversed(range(layout.count())):
            item = layout.itemAt(i)
            if type(item) == QVBoxLayout or type(item) == QHBoxLayout:
                self.recursive_clean(item)
                item.setParent(None)
            else:
                item.widget().setParent(None)

    def clean_central_layout(self):
        self.recursive_clean(self.central_widget.layout())

    def start_game_gener(self, level_id):
        def res():
            GameWindow(level_id, self).show()
            self.hide()

        return res
