from flask import Flask
from flask import request, render_template

import sudoku_solver

app = Flask(__name__)


@app.route('/')
def index():
    return render_template('index.html')


@app.route('/solve', methods=['GET'])
def solve():
    data = request.args['data']
    split_data = [int(a) for a in data.split()]
    block_size = int(round(len(split_data) ** 0.25))
    num_data = [[0] * (block_size ** 2) for _ in range(block_size ** 2)]
    for i in range(block_size ** 2):
        for j in range(block_size ** 2):
            a = (i // block_size)
            b = j // block_size
            block_num = a * block_size + b
            block_index = (i - a * block_size) * block_size + (j - b * block_size)
            num_data[i][j] = split_data[block_num * block_size ** 2 + block_index]

    valid_1 = True
    for i in range(block_size ** 2):
        v = has_coinciding(split_data[i * (block_size ** 2): (i + 1) * (block_size ** 2)])
        if v:
            valid_1 = False
            break
    if not valid_1:
        return pack_data(num_data)

    valid_2 = True
    for i in range(block_size ** 2):
        v = has_coinciding(num_data[i])
        if v:
            valid_2 = False
            break
    if not valid_2:
        return pack_data(num_data)

    valid_3 = True
    for i in range(block_size ** 2):
        v = has_coinciding([row[i] for row in num_data])
        if v:
            valid_3 = False
            break
    if not valid_3:
        return pack_data(num_data)

    sudoku_solver.solveSudoku(num_data)
    return pack_data(num_data)


def pack_data(array):
    res = [' ' for _ in range(len(array) ** 2)]
    block_size = int(round(len(res) ** 0.25))
    for i in range(block_size ** 2):
        for j in range(block_size ** 2):
            a = (i // block_size)
            b = j // block_size
            block_num = a * block_size + b
            block_index = (i - a * block_size) * block_size + (j - b * block_size)
            res[block_num * block_size ** 2 + block_index] = str(array[i][j])
    print(res)
    return ' '.join(res)


def has_coinciding(array):
    arr = sorted(array)
    for i in range(len(arr) - 1):
        if arr[i] != 0 and arr[i] == arr[i + 1]:
            return True
    return False


if __name__ == '__main__':
    app.run(debug=True)
