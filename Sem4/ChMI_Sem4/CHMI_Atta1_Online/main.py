import math
import sys

from PyQt5.QtWidgets import QMainWindow, QDesktopWidget, QVBoxLayout, QHBoxLayout, QPushButton, QSpinBox, QLabel, \
    QDialog, QMessageBox, QApplication, QLineEdit
from matplotlib.backends.backend_qt5agg import FigureCanvasQTAgg as FigureCanvas
from matplotlib.backends.backend_qt5agg import NavigationToolbar2QT as NavigationToolbar
import matplotlib.pyplot as plt
from tkinter import Tk
from tkinter.filedialog import askopenfilename
import numpy as np


class FormMain(QDialog):

    def __init__(self):
        super(FormMain, self).__init__()
        self.initUI()

    def initUI(self):
        self.setWindowTitle('Фазовый портрет эксцессов')
        self.resize(800, 800)
        layout = QVBoxLayout()

        self.data = ()
        self.button_load_file = QPushButton('Загрузить данные из файла')
        self.spinbox_window_size = QSpinBox()
        self.spinbox_window_size.setValue(10)
        self.spinbox_window_size_label = QLabel('Размер окна измерения эксцесса:')
        self.spinbox_window_size.valueChanged.connect(self.plot)
        self.button_load_file.clicked.connect(self.open_file)

        self.figure_rr = plt.figure(0)
        self.canvas_rr = FigureCanvas(self.figure_rr)
        self.toolbar_rr = NavigationToolbar(self.canvas_rr, self)

        self.figure_excess = plt.figure(1)
        self.canvas_excess = FigureCanvas(self.figure_excess)
        self.toolbar_excess = NavigationToolbar(self.canvas_excess, self)

        layout.addWidget(self.toolbar_rr)
        layout.addWidget(self.canvas_rr)
        layout.addWidget(self.toolbar_excess)
        layout.addWidget(self.canvas_excess)

        hbox_layout = QHBoxLayout()
        hbox_layout.addWidget(self.button_load_file)
        hbox_layout.addWidget(self.spinbox_window_size_label)
        hbox_layout.addWidget(self.spinbox_window_size)
        layout.addLayout(hbox_layout)

        hbox_layout_sin = QHBoxLayout()
        self.button_plot_sin = QPushButton("Построить функцию y = sin(wx)")
        self.button_plot_sin.clicked.connect(self.build_sin)
        hbox_layout_sin.addWidget(self.button_plot_sin)
        hbox_layout_sin.addWidget(QLabel("w:"))
        self.lineedit_frequency = QLineEdit()
        self.lineedit_frequency.setText("0.001")
        hbox_layout_sin.addWidget(self.lineedit_frequency)
        layout.addLayout(hbox_layout_sin)

        self.setLayout(layout)

    def center(self):
        screen = QDesktopWidget().screenGeometry()
        size = self.geometry()
        self.move((screen.width() - size.width()) // 2,
                  (screen.height() - size.height()) // 2)

    def open_file(self):
        root = Tk()
        root.withdraw()
        root.update()
        pathString = askopenfilename(filetypes=[("RR interval files", "*.rr"), ("Text files", "*.txt")])
        if pathString:
            try:
                openFile = open(pathString, 'r')
                intervals = [int(line.split()[0]) for line in [_ for _ in openFile][1:]]
                self.spinbox_window_size.setRange(4, len(intervals) - 2)
                sums = []
                s = 0
                for interval in intervals:
                    s += interval
                    sums.append(s)

                self.data = (sums, intervals)
                self.plot()
            except FileNotFoundError:
                print("File not found")
            except ValueError:
                msg = QMessageBox()
                msg.setIcon(QMessageBox.Critical)
                msg.setText("Неверный формат содержания файла")
                msg.setWindowTitle("Ошибка")
                msg.exec_()
        root.destroy()

    def plot(self):
        if not self.data:
            return
        self.figure_rr.clear()
        rr_plotter = self.figure_rr.add_subplot(111)
        rr_plotter.set_title("График RR-интервалов")
        rr_plotter.plot(*self.data)
        self.canvas_rr.draw()

        excesses = self.excess_row(self.data[1], self.spinbox_window_size.value())
        self.figure_excess.clear()
        excess_plotter = self.figure_excess.add_subplot(111)
        excess_plotter.set_title("Фазовый портрет эксцессов")
        excess_plotter.plot(excesses[1:], excesses[:-1], linewidth=0.5)
        self.canvas_excess.draw()

    def excess(self, values):
        D = np.var(values)
        M = np.mean(values)
        mu4 = 0
        for x in values:
            mu4 += (x - M) ** 4
        mu4 /= len(values)
        return mu4 / (D ** 2) - 3

    def excess_row(self, data, window):
        res = []
        for i in range(len(data) - window):
            sample = data[i:i + window]
            res.append(self.excess(sample))
        return res

    def build_sin(self):
        w = float(self.lineedit_frequency.text())
        self.data = ([i for i in range(450)], [math.sin(w*i) for i in range(450)])
        self.plot()


if __name__ == '__main__':
    app = QApplication(sys.argv)
    game = FormMain()
    game.show()
    sys.exit(app.exec_())
