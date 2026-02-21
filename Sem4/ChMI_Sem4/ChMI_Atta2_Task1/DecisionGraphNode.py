class DecisionGraphNode:
    def __init__(self, question: str):
        self.question = question
        self.children = {}

    def add_child(self, answer: str, child):
        self.children[answer] = child

    def is_verdict(self):
        return not self.children

    @staticmethod
    def parse_graph_from_file(filename):
        file = open(filename, encoding='UTF-8')
        line = file.readline()

        questions = []
        while line.strip() != '':
            questions.append(DecisionGraphNode(line.strip()))
            line = file.readline()

        line = file.readline()
        while line.strip() != '':
            data = line.split('->')
            src_id = int(data[0])
            dst_id = int(data[2])
            answer = data[1].strip()
            questions[src_id - 1].add_child(answer, questions[dst_id - 1])
            line = file.readline()

        return questions[0]



