import PyQt5.QtCore
from PyQt5.QtWidgets import QPushButton, QMainWindow, QLabel, QHBoxLayout
from PyQt5.QtWidgets import QVBoxLayout

from DecisionGraphNode import DecisionGraphNode


class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle(" ")
        self.parse_graph("graph.txt")
        self.nodes_chain = []
        self.setGeometry(200, 200, 1000, 700)
        self.setStyleSheet("QMainWindow{background-image: url(bg3.png);}")
        funny = QVBoxLayout()
        funny.setAlignment(PyQt5.QtCore.Qt.AlignCenter)
        self.central_widget = QLabel(self)
        self.central_widget.setLayout(funny)
        self.setCentralWidget(self.central_widget)
        self.reset_examination()

    def parse_graph(self, filename: str):
        self.root_node = DecisionGraphNode.parse_graph_from_file(filename)

    def reset_examination(self):
        self.nodes_chain = []
        button_begin_examination = QPushButton("НАЧАТЬ ВЫЯВЛЕНИЕ ДИАГНОЗА")
        button_begin_examination.setStyleSheet("QPushButton{border: 6px solid black;"
                                               "border-radius: 15px;"
                                               "background: #ff6666;"
                                               "padding:30px;"
                                               "font-size:30px;"
                                               "font-weight: 900;"
                                               "color: white;}"
                                               ""
                                               "QPushButton::hover{"
                                               "border-color:white;}")
        button_begin_examination.clicked.connect(self.start_examination)
        self.clean_central_layout()
        self.central_widget.layout().addWidget(button_begin_examination)

    def set_node(self, node: DecisionGraphNode):
        if (not self.nodes_chain) or self.nodes_chain[-1] != node:
            self.nodes_chain.append(node)
        form = QVBoxLayout()

        form.setAlignment(PyQt5.QtCore.Qt.AlignCenter)
        hbox_controls = QHBoxLayout()
        if len(self.nodes_chain) > 1:
            btn_return = QPushButton("Назад")
            btn_return.clicked.connect(self.set_previous_node)
            btn_return.setStyleSheet("QPushButton{border:5px solid black;"
                                     "font-size:20px;"
                                     "border-radius: 10px;"
                                     "background: #ffc966;"
                                     "padding:10px;"
                                     "margin:5px;}"
                                     "QPushButton::hover{"
                                     "color:#ffc966;"
                                     "background:white;}")
            hbox_controls.addWidget(btn_return)
        hbox_controls.addWidget(QLabel())
        btn_reset = QPushButton("Сброс")
        btn_reset.clicked.connect(self.reset_examination)
        btn_reset.setStyleSheet("QPushButton{border:5px solid black;"
                                "font-size:20px;"
                                "border-radius: 10px;"
                                "background: #ff6666;"
                                "padding:10px;"
                                "margin:5px;}"
                                "QPushButton::hover{"
                                "color:#ff6666;"
                                "background:white;}")
        hbox_controls.addWidget(btn_reset)
        form.addLayout(hbox_controls)

        label = QLabel(node.question + ('' if node.is_verdict() else ':'))
        label.setAlignment(PyQt5.QtCore.Qt.AlignCenter)
        label.setStyleSheet("color: black;"
                            "font-size:30px;"
                            "margin:5px;"
                            "padding:10px;"
                            "background:white;"
                            "border:5px solid black;"
                            "border-radius: 10px;")
        form.addWidget(label)

        def make_answer_btn(pair):
            btn = QPushButton(pair[0])
            btn.setStyleSheet("QPushButton{border:5px solid black;"
                              "font-size:30px;"
                              "border-radius: 10px;"
                              "background: #90ee90;"
                              "padding:10px;"
                              "margin:5px;}"
                              "QPushButton::hover{"
                              "color:#90ee90;"
                              "background:white;}")

            def click():
                self.set_node(pair[1])

            btn.clicked.connect(click)
            return btn

        if not node.is_verdict():
            hbox_buttons = QHBoxLayout()

            hbox_buttons.setAlignment(PyQt5.QtCore.Qt.AlignCenter)
            for pair in node.children.items():
                btn = make_answer_btn(pair)
                hbox_buttons.addWidget(btn)
            form.addLayout(hbox_buttons)

        self.clean_central_layout()
        self.central_widget.layout().addLayout(form)

    def start_examination(self):
        self.set_node(self.root_node)

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

    def set_previous_node(self):
        del self.nodes_chain[-1]
        self.set_node(self.nodes_chain[-1])
