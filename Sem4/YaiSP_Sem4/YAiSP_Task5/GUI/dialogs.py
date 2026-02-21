from PyQt5 import QtCore
from PyQt5.QtGui import QColor
from PyQt5.QtWidgets import QDialog, QVBoxLayout, QLabel, QHBoxLayout, QPushButton, QColorDialog, QMessageBox, QTextEdit


class DialogHowToPlay(QDialog):
    def __init__(self):
        super().__init__()
        self.layout = QVBoxLayout()
        self.setWindowTitle("Правила игры")
        self.setStyleSheet("QLabel{"
                           "font-family: Comic Sans MS;"
                           "font-size:20px;"
                           "padding: 5px;"
                           "margin: 5px;"
                           "border: 2px solid black;"
                           "border-radius: 5px;"
                           "background: #98fb98;"
                           "}"
                           "QDialog{"
                           "background:green;"
                           "}")
        self.layout.setAlignment(QtCore.Qt.AlignCenter)
        self.layout.addWidget(QLabel('1. Нажимая на клавиши "Вверх", "Вниз", "Влево", "Вправо" на клавиатуре,'
                                     '\nдвигайте персонажа по полю'))
        self.layout.addWidget(QLabel('2. Составляйте линии из кубиков одинаковых цветов,'
                                     '\nлинии длинной более 3 уничтожаются'))
        self.layout.addWidget(QLabel('3. Раз в несколько секунд сверху падают новые ряды из кубиков '))
        self.layout.addWidget(QLabel('4. Чтобы пройти уровень, не допустите того, '
                                     'чтобы какой-либо из столбцов\n поля заполнился до истечения времени'))
        self.setLayout(self.layout)


class DialogAboutDeveloper(QDialog):
    def __init__(self):
        super().__init__()
        self.layout = QVBoxLayout()
        self.setStyleSheet("QLabel{"
                           "font-family: Comic Sans MS;"
                           "font-size:30px;"
                           "padding: 5px;"
                           "margin: 5px;"
                           "border: 2px solid black;"
                           "border-radius: 5px;"
                           "background: #98fb98;"
                           "}"
                           "QDialog{"
                           "background:green;"
                           "}")
        self.layout.setAlignment(QtCore.Qt.AlignCenter)
        self.lbl = QLabel('Разработчик: студент 2 курса факультета компьютерных наук ВГУ\n'
                          'Ячный Марк Алексеевич')
        self.lbl.setAlignment(QtCore.Qt.AlignCenter)
        self.lbl2 = QLabel('<a href="https://vk.com/sinus_prime"> ВКонтакте </a>')
        self.lbl2.setOpenExternalLinks(True)
        self.lbl2.setAlignment(QtCore.Qt.AlignCenter)
        self.layout.addWidget(self.lbl)
        self.layout.addWidget(self.lbl2)
        self.setLayout(self.layout)
        self.setWindowTitle("О разработчике")


class DialogColorSettings(QDialog):
    def __init__(self):
        super().__init__()
        self.layout = QVBoxLayout()
        self.setWindowTitle("Настройка цветов")
        self.setStyleSheet("QLabel{"
                           "font-family: Comic Sans MS;"
                           "font-size:30px;"
                           "padding: 5px;"
                           "margin: 5px;"
                           "border: 2px solid black;"
                           "border-radius: 5px;"
                           "background: #98fb98;"
                           "}"
                           "QDialog{"
                           "background:green;"
                           "}"
                           "QPushButton{"
                           "background:red;"
                           "color:white;"
                           "font-family: Comic Sans MS;"
                           "font-size:30px;"
                           "padding: 5px;"
                           "margin: 5px;"
                           "border: 2px solid black;"
                           "border-radius: 5px;"
                           "}"
                           "QPushButton::hover{"
                           "background:white;"
                           "color:red;"
                           "}")
        self.layout.setAlignment(QtCore.Qt.AlignCenter)
        self.lbl = QLabel('Настройки цветов')
        self.lbl.setAlignment(QtCore.Qt.AlignCenter)
        self.layout.addWidget(self.lbl)
        lines = [_ for _ in open('../colors.txt')]
        colors = [QColor(*map(int, line.split())) for line in lines]
        labels = [QLabel() for c in colors]

        def set_actual_colors():
            for i in range(len(labels)):
                labels[i].setStyleSheet("border: 2px solid black;"
                                        f"background: rgb({colors[i].red()}, {colors[i].green()}, {colors[i].blue()});")
                labels[i].setFixedSize(60, 60)

        def listener_i(i):
            def res():
                dlg = QColorDialog()
                clr = dlg.getColor()
                if clr.isValid():
                    colors[i] = clr
                set_actual_colors()

            return res

        set_actual_colors()

        for i in range(len(colors)):
            hl = QHBoxLayout()
            hl.addWidget(labels[i])
            self.layout.addLayout(hl)
            btn = QPushButton(f"Изменить {i} цвет")
            btn.clicked.connect(listener_i(i))
            hl.addWidget(btn)

        def save():
            sorted_colors = sorted(colors, key=lambda c: (c.red(), c.green(), c.blue()))
            valid = True
            for i in range(len(sorted_colors) - 1):
                if sorted_colors[i] == sorted_colors[i + 1]:
                    valid = False
                    break

            if valid:
                file = open('../colors.txt', 'w')
                for c in colors:
                    file.write(f"{c.red()} {c.green()} {c.blue()}\n")
                self.close()
            else:
                msg = QMessageBox()
                msg.setIcon(QMessageBox.Critical)
                msg.setText("Цвета не должны быть одинаковыми")
                msg.setInformativeText("")
                msg.setWindowTitle("Ошибка")
                msg.exec_()


        save_btn = QPushButton("СОХРАНИТЬ")
        save_btn.setStyleSheet("QPushButton{"
                               "background:green;"
                               "color:black;"
                               "background: #98fb98;"
                               "font-family: Comic Sans MS;"
                               "font-size:30px;"
                               "padding: 5px;"
                               "margin: 5px;"
                               "border: 2px solid black;"
                               "border-radius: 5px;"
                               "}"
                               "QPushButton::hover{"
                               "background:white;"
                               "}")
        save_btn.clicked.connect(save)
        self.layout.addWidget(save_btn)
        self.setLayout(self.layout)
