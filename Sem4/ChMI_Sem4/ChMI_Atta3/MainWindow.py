import socket

from PyQt5 import QtCore
from PyQt5.QtCore import QThread
from PyQt5.QtWidgets import QMainWindow, QHBoxLayout, QWidget, QGridLayout, QPushButton, QLineEdit, QLabel, QTextEdit, \
    QVBoxLayout

from GraphWindow import GraphWindow
from MigalkaWindow import MigalkaWindow


class MainWindow(QMainWindow):
    name = "Data_Receiver"
    channels = [16]
    sock = None
    host = None

    thread = None

    def __init__(self) -> None:
        super().__init__()
        self.files = None
        self.need_to_begin_new_tick = False
        self.recording = False
        self.sock = socket.socket()
        self.connected = False
        self.init_gui()
        self.thread = self.MyThread(self)
        self.thread.start()

    def init_gui(self):
        self.layout = QHBoxLayout()
        self.central_widget = QWidget()
        self.network_layout = QGridLayout()
        self.network_layout.setAlignment(QtCore.Qt.AlignTop)
        self.input_port = QLineEdit("9000")
        self.input_port.setFixedWidth(100)
        self.input_host = QLineEdit("127.0.0.1")
        self.input_host.setFixedWidth(100)
        self.button_connect = QPushButton("ПОДКЛЮЧИТЬСЯ")
        self.network_layout.addWidget(QLabel("ХОСТ"), 0, 0)
        self.network_layout.addWidget(QLabel("ПОРТ"), 0, 1)
        self.network_layout.addWidget(self.input_host, 1, 0)
        self.network_layout.addWidget(self.input_port, 1, 1)
        self.network_layout.addWidget(self.button_connect, 2, 0, 1, 2)

        self.layout.addLayout(self.network_layout)
        self.buttons_layout = QVBoxLayout()

        self.button_open_migalka = QPushButton("Открыть мигающую форму")
        self.button_open_migalka.clicked.connect(lambda :MigalkaWindow(self).show())
        self.buttons_layout.addWidget(self.button_open_migalka)
        self.button_start_eeg = QPushButton("НАЧАТЬ ЗАПИСЬ")
        self.button_start_eeg.clicked.connect(self.on_click_btn_start_eeg)
        self.buttons_layout.addWidget(self.button_start_eeg)
        self.layout.addLayout(self.buttons_layout)
        self.buttons_layout.setAlignment(QtCore.Qt.AlignTop)
        self.central_widget.setLayout(self.layout)
        self.setCentralWidget(self.central_widget)
        self.button_connect.clicked.connect(self.on_click)
        self.button_plot = QPushButton("Показать графики")
        self.buttons_layout.addWidget(self.button_plot)
        def open_graph():
            gw = GraphWindow(self, self.channels)
            gw.show()
        self.button_plot.clicked.connect(open_graph)

    def loop_event(self):
        if self.connected:

            date = self.sock.recv(4)
            n = int.from_bytes(date, 'little')

            if n == 0:
                date = self.sock.recv(4)
                n = int.from_bytes(date, 'little')
                if n != 0:
                    if date != b'\xff\xff\xff\x7f':
                        id = int.from_bytes(self.sock.recv(4), 'little')
                        length = int.from_bytes(self.sock.recv(4), 'little')
                        server_name = self.byte_to_string(self.sock.recv(length * 2))
                else:
                    return
            else:
                length = int.from_bytes(self.sock.recv(4), 'little')
                raw_data = self.sock.recv(length)
                if self.recording:
                    for i in range(len(self.channels)):
                        channel = self.channels[i]
                        decoded_data = self.get_data(raw_data, channel)
                        self.files[i].write(decoded_data + ' ')
                        if self.need_to_begin_new_tick:
                            self.files[i].write('\n')
                        self.files[i].flush()
                    self.need_to_begin_new_tick = False




    def on_click(self):
        if not self.connected:
            self.sock.connect((self.input_host.text(), int(self.input_port.text())))
            self.sock.send(len(self.name).to_bytes(length=4, byteorder='little'))
            self.sock.send(self.string_to_byte(self.name))
            self.button_connect.setText("ОТКЛЮЧИТЬСЯ")
            self.connected = True
        else:
            self.thread.stop()
            self.sock.close()
            self.connected = False

    # ИСПРАВЛЕННАЯ
    def start_new_tick(self):
        self.need_to_begin_new_tick = True

    def on_click_btn_start_eeg(self):
        if not self.recording:
            self.recording = True
            self.files = [open(f'channel{i}.txt', 'w') for i in self.channels]
            self.button_start_eeg.setText("ОСТАНОВИТЬ ЗАПИСЬ")
        else:
            self.recording = False
            self.button_start_eeg.setText("НАЧАТЬ ЗАПИСЬ")
    @staticmethod
    def get_data(input, channel_index):
        start_index = 68 + channel_index * 24 * 2
        rr_date_len = 24 * 2
        input = input[start_index: start_index + rr_date_len]

        date = ' '.join([str(int.from_bytes(input[i:i + 2], byteorder='little', signed=True)) for i in range(0, rr_date_len, 2)])

        return date

    @staticmethod
    def string_to_byte(t: str) -> bytearray:
        arr = bytearray(len(t) * 2)
        arr[::2] = t.encode()
        return arr

    @staticmethod
    def byte_to_string(b) -> str:
        arr = bytearray(b)
        return arr[::2].decode()

    class MyThread(QThread):
        def __init__(self, main_window):
            super().__init__()
            self._running = False
            self.main_window = main_window

        def run(self):
            self._running = True
            while self._running:
                self.main_window.loop_event()

        def stop(self):
            self._running = False
