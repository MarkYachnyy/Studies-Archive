import time

from PyQt5.QtCore import QThread
from PyQt5.QtWidgets import QMainWindow, QHBoxLayout, QWidget, QLabel
from matplotlib.backends.backend_qt5agg import FigureCanvasQTAgg as FigureCanvas
import matplotlib.pyplot as plt

class GraphWindow(QMainWindow):
    def __init__(self, mw, channels):
        super().__init__()
        self.mw = mw
        self.channels = channels
        self.layout = QHBoxLayout()
        self.central_widget = QWidget()
        for channel in channels:
            figure = plt.figure()
            canvas = FigureCanvas(figure)
            figure.clear()
            ax = figure.add_subplot(111)

            file = open(f"channel{channel}.txt", 'r')
            lines = [_ for _ in file][5:-1]
            values = [[int(a) for a in line.split()] for line in lines]
            min_len = len(values[0])
            for v in values:
                min_len = min(min_len, len(v))
            data = [0] * min_len
            for v in values:
                slice = v[0:min_len]
                for i in range(min_len):
                    data[i] += slice[i]
            for i in range(len(data)):
                data[i] /= min_len

            window_size = 102
            smooth_data = [0] * (len(data) - window_size + 1)
            s = sum(data[:window_size])
            for i in range(len(smooth_data) - 1):
                smooth_data[i] = s / window_size
                s -= data[i]
                s += data[i + window_size]

            del smooth_data[-1]
            ax.plot(smooth_data[1:], smooth_data[:-1])
            self.layout.addWidget(canvas)
            canvas.draw()

            figure2 = plt.figure()
            figure2.clear()
            ax2 = figure2.add_subplot(111)
            ax2.plot(smooth_data)

            plt.show()
        self.layout.addWidget(QLabel())
        self.central_widget.setLayout(self.layout)
        self.setCentralWidget(self.central_widget)

