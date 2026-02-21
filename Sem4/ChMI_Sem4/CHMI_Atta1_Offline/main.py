import sys

from PyQt5.QtGui import QFont
from PyQt5.QtWidgets import QDesktopWidget, QVBoxLayout, QHBoxLayout, QPushButton, QSpinBox, QLabel, \
    QDialog, QMessageBox, QApplication, QGridLayout
from matplotlib.backends.backend_qt5agg import FigureCanvasQTAgg as FigureCanvas
from matplotlib.backends.backend_qt5agg import NavigationToolbar2QT as NavigationToolbar
import matplotlib.pyplot as plt
from tkinter import Tk
from tkinter.filedialog import askopenfilename
from sklearn.discriminant_analysis import QuadraticDiscriminantAnalysis
from sklearn.model_selection import train_test_split


class FormMain(QDialog):

    def __init__(self):
        super(FormMain, self).__init__()
        self.initUI()

    def initUI(self):
        self.setWindowTitle('Разделение RR-интервалов с использованием НДА')
        self.resize(800, 800)
        layout = QVBoxLayout()

        self.data = ()

        self.figure_rr = plt.figure(0)
        self.canvas_rr = FigureCanvas(self.figure_rr)
        self.toolbar_rr = NavigationToolbar(self.canvas_rr, self)
        layout.addWidget(self.toolbar_rr)
        layout.addWidget(self.canvas_rr)

        hbox_layout = QHBoxLayout()
        vbox_layout = QVBoxLayout()
        self.button_load_prepared_data = QPushButton("Загрузить обработанные данные")
        self.button_load_prepared_data.clicked.connect(self.read_and_classify_prepared_data)
        self.button_load_file = QPushButton('Загрузить данные из файла')
        self.button_load_file.clicked.connect(self.open_file_dialog)
        vbox_layout.addWidget(self.button_load_file)
        vbox_layout.addWidget(self.button_load_prepared_data)
        hbox_layout.addLayout(vbox_layout)

        grid_layout = QGridLayout()
        grid_layout.addWidget(QLabel("Время начала напряжённого состояния (сек):"), 0, 0)
        self.spinbox_thinking_start = QSpinBox()
        self.spinbox_thinking_start.setRange(0, 300)
        self.spinbox_thinking_start.setValue(180)
        grid_layout.addWidget(self.spinbox_thinking_start, 0, 1)

        grid_layout.addWidget(QLabel("Время расслабления (сек):"), 1, 0)
        self.spinbox_thinking_end = QSpinBox()
        self.spinbox_thinking_end.setRange(0, 300)
        self.spinbox_thinking_end.setValue(225)
        grid_layout.addWidget(self.spinbox_thinking_end, 1, 1)

        hbox_layout.addLayout(grid_layout)
        layout.addLayout(hbox_layout)

        self.button_classify = QPushButton("Разделить интервалы")
        self.button_classify.clicked.connect(self.classify)
        hbox_layout.addWidget(self.button_classify)

        self.figure_classifier = plt.figure(1)
        self.canvas_classifier = FigureCanvas(self.figure_classifier)
        self.toolbar_classifier = NavigationToolbar(self.canvas_classifier, self)
        layout.addWidget(self.toolbar_classifier)
        layout.addWidget(self.canvas_classifier)

        self.label_sens_and_spec = QLabel("")
        self.label_sens_and_spec.setFont(QFont("Arial", 15))
        layout.addWidget(self.label_sens_and_spec)

        self.setLayout(layout)

    def center(self):
        screen = QDesktopWidget().screenGeometry()
        size = self.geometry()
        self.move((screen.width() - size.width()) // 2,
                  (screen.height() - size.height()) // 2)

    def open_file_dialog(self):
        root = Tk()
        root.withdraw()
        root.update()
        pathString = askopenfilename(filetypes=[("RR interval files", "*.rr"), ("Text files", "*.txt")], initialdir='.')
        if pathString:
            try:
                self.open_file(pathString)
            except FileNotFoundError:
                print("File not found")
            except ValueError:
                msg = QMessageBox()
                msg.setIcon(QMessageBox.Critical)
                msg.setText("Неверный формат содержания файла")
                msg.setWindowTitle("Ошибка")
                msg.exec_()
        root.destroy()

    def open_file(self, pathString):
        input_file = open(pathString, 'r')
        intervals = [int(line.split()[0]) for line in [_ for _ in input_file][1:]]
        sums = []
        s = 0
        for interval in intervals:
            if interval < 1:
                raise ValueError
            s += interval
            sums.append(s)
        self.data = (sums, intervals)
        self.plot_rr()

    def classify(self):
        if not self.data:
            return

        y = self.get_classes_of_data(self.data[1][1:], self.spinbox_thinking_start.value(),
                                     self.spinbox_thinking_end.value())
        if max(y) == min(y):
            msg = QMessageBox()
            msg.setIcon(QMessageBox.Critical)
            msg.setText("В окно попадают интервалы только одного класса, измените время начала или конца")
            msg.setWindowTitle("Ошибка")
            msg.exec_()
            return

        X = [(self.data[1][i - 1], self.data[1][i]) for i in range(1, len(self.data[1]))]
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.4, random_state=42)
        qda = QuadraticDiscriminantAnalysis()
        qda.fit(X_train, y_train)

        min_rr = min(self.data[1])
        max_rr = max(self.data[1])

        sens, spec = self.sens_and_spec(qda, X_test, y_test)
        self.label_sens_and_spec.setText(f"Чувствительность = {round(sens, 3)}; Специфичность = {round(spec, 3)}")

        points = []
        for i in range(min_rr - 10, max_rr + 10):
            for j in range(min_rr - 10, max_rr + 10):
                points.append((i, j))

        predictions = qda.predict(points)
        points_1 = []
        points_m1 = []
        for i in range(len(predictions)):
            if predictions[i] == 1:
                points_1.append(points[i])
            else:
                points_m1.append(points[i])

        self.figure_classifier.clear()
        classifier_plotter = self.figure_classifier.add_subplot(111)
        classifier_plotter.set_title("Разделение RR-интервалов")
        classifier_plotter.set_xlabel('RR(n)')
        classifier_plotter.set_ylabel('RR(n-1)')
        classifier_plotter.scatter([a[0] for a in points_1], [a[1] for a in points_1], c='salmon')
        classifier_plotter.scatter([a[0] for a in points_m1], [a[1] for a in points_m1], c='mediumspringgreen')

        data_2d = [(X[i], y[i]) for i in range(len(X))]
        data_m1 = list(filter(lambda a: a[1] == -1, data_2d))
        data_1 = list(filter(lambda a: a[1] == 1, data_2d))
        x_m1 = [a[0][0] for a in data_m1]
        y_m1 = [b[0][1] for b in data_m1]
        x_m2 = [a[0][0] for a in data_1]
        y_m2 = [a[0][1] for a in data_1]
        classifier_plotter.scatter(x_m1, y_m1, s=3, c='green')
        classifier_plotter.scatter(x_m2, y_m2, s=3, c='red')

        self.canvas_classifier.draw()

    def get_classes_of_data(self, data, thinking_start_sec: int, thinking_end_sec: int):
        res = []
        s = 0
        for a in data:
            s += a
            if s > thinking_end_sec * 1000:
                res.append(-1)
            elif s > thinking_start_sec * 1000:
                res.append(1)
            else:
                res.append(-1)
        return res

    def get_classifier(self, X, y):
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.4, random_state=42)
        qda = QuadraticDiscriminantAnalysis()
        qda.fit(X_train, y_train)
        return qda

    def plot_rr(self):
        if not self.data:
            return
        self.figure_rr.clear()
        rr_plotter = self.figure_rr.add_subplot(111)
        rr_plotter.set_xlabel("Время")
        rr_plotter.set_ylabel("RR-интервал")
        rr_plotter.set_title("RR-интервалы")
        rr_plotter.scatter(*self.data)
        self.canvas_rr.draw()

    def sens_and_spec(self, classifier, X_test, y_test):
        y_pred = classifier.predict(X_test)
        tp, tn, fp, fn = 0, 0, 0, 0
        for i in range(len(y_pred)):
            pred = y_pred[i]
            fact = y_test[i]
            if pred == 1 and fact == 1:
                tp += 1
            elif pred == 1 and fact == -1:
                fp += 1
            elif pred == -1 and fact == -1:
                tn += 1
            elif pred == -1 and fact == 1:
                fn += 1

        return (tp / (tp + fn), tn / (tn + fp))

    def read_prepared_data(self, filename: str):
        res = []
        try:
            for line in open(filename, 'r'):
                x1, x2, y = map(int, line.split())
                res.append(((x1, x2), y))
        except Exception as e:
            print(e)
        return res

    def read_and_classify_prepared_data(self):
        root = Tk()
        root.withdraw()
        root.update()
        pathString = askopenfilename(filetypes=[("Text files", "*.txt")], initialdir='.')
        if pathString:
            try:
                p_data = self.read_prepared_data(pathString)
                self.figure_rr.clear()
                y = [sample[1] for sample in p_data]
                X = [sample[0] for sample in p_data]
                X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.4, random_state=42)
                qda = QuadraticDiscriminantAnalysis()
                qda.fit(X_train, y_train)

                min_rr = 0
                max_rr = 400

                sens, spec = self.sens_and_spec(qda, X_test, y_test)
                self.label_sens_and_spec.setText(f"Чувствительность = {round(sens, 3)}; Специфичность = {round(spec, 3)}")

                points = []
                for i in range(min_rr - 10, max_rr + 10):
                    for j in range(min_rr - 10, max_rr + 10):
                        points.append((i, j))

                predictions = qda.predict(points)
                points_1 = []
                points_m1 = []
                for i in range(len(predictions)):
                    if predictions[i] == 1:
                        points_1.append(points[i])
                    else:
                        points_m1.append(points[i])

                self.figure_classifier.clear()
                self.canvas_rr.draw()
                classifier_plotter = self.figure_classifier.add_subplot(111)
                classifier_plotter.set_title("Разделение RR-интервалов")
                classifier_plotter.set_xlabel('RR(n)')
                classifier_plotter.set_ylabel('RR(n-1)')
                classifier_plotter.scatter([a[0] for a in points_1], [a[1] for a in points_1], c='salmon')
                classifier_plotter.scatter([a[0] for a in points_m1], [a[1] for a in points_m1], c='mediumspringgreen')

                data_2d = [(X[i], y[i]) for i in range(len(X))]
                data_m1 = list(filter(lambda a: a[1] == -1, data_2d))
                data_1 = list(filter(lambda a: a[1] == 1, data_2d))
                x_m1 = [a[0][0] for a in data_m1]
                y_m1 = [b[0][1] for b in data_m1]
                x_m2 = [a[0][0] for a in data_1]
                y_m2 = [a[0][1] for a in data_1]
                classifier_plotter.scatter(x_m1, y_m1, s=3, c='green')
                classifier_plotter.scatter(x_m2, y_m2, s=3, c='red')

                self.canvas_classifier.draw()
            except Exception as e:
                print(e)


if __name__ == '__main__':
    app = QApplication(sys.argv)
    game = FormMain()
    game.show()
    sys.exit(app.exec_())
