class MatrixSizeMismatchError(Exception):
    def __init__(self, text):
        self.txt = text


def matrix_to_str(matrix):
    return "\n".join(["\t".join([str(a) for a in line]) for line in matrix])


def read_matrix_from_file(filename):
    file = open(filename)
    res = []
    length = -1
    for line in file:
        row = [int(a) for a in line.split()]
        if -1 < length != len(row):
            raise MatrixSizeMismatchError('')
        length = len(row)
        res.append(row)
    file.close()
    return res


def remove_min_max(matrix):
    max_el = matrix[0][0] if len(matrix) > 0 else 0
    min_el = matrix[0][0] if len(matrix) > 0 else 0

    rows = set()
    columns = set()

    for row in matrix:
        for a in row:
            max_el = max(max_el, a)
            min_el = min(min_el, a)

    for i in range(len(matrix)):
        for j in range(len(matrix[0])):
            if matrix[i][j] == min_el or matrix[i][j] == max_el:
                rows.add(i)
                columns.add(j)

    # max_rows = set()
    # max_columns = set()
    # min_rows = set()
    # min_columns = set()
    #
    # for i in range(len(matrix)):
    #     for j in range(len(matrix[0])):
    #         a = matrix[i][j]
    #         if a >= max_el:
    #             if a > max_el:
    #                 max_rows = set()
    #                 max_columns = set()
    #                 max_el = a
    #             max_rows.add(i)
    #             max_columns.add(j)
    #
    #         if a <= min_el:
    #             if a < min_el:
    #                 min_rows = set()
    #                 min_columns = set()
    #                 min_el = a
    #             min_rows.add(i)
    #             min_columns.add(j)
    #
    # rows = max_rows.union(min_rows)
    # columns = max_columns.union(min_columns)

    res = []

    for i in range(len(matrix)):
        if i not in rows:
            row = []
            for j in range(len(matrix[0])):
                if j not in columns:
                    row.append(matrix[i][j])
            res.append(row)

    return res


def task_2(input_file_name):
    try:
        matrix = read_matrix_from_file(input_file_name)
        matrix1 = remove_min_max(matrix)
        output_file = open("output.txt", 'w')
        output_file.write(matrix_to_str(matrix1))
        output_file.close()
    except FileNotFoundError:
        print('Error: Specified input file does not exist')
    except ValueError:
        print('Error: Cannot parse the input file content')
    except MatrixSizeMismatchError:
        print('Error: Rows of input matrix have different sizes')


task_2("input03.txt")
