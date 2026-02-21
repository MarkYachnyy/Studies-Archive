def choose_coinciding(list1, list2):
    res = []
    for i in range(min(len(list1), len(list2))):
        if list1[i] == list2[i]:
            res.append(list1[i])
    return res


def parse_list(line):
    return [int(a) for a in line.split()]


def task_1(input_file_name):
    try:
        input_file = open(input_file_name)
        list1 = parse_list(input_file.readline())
        list2 = parse_list(input_file.readline())
        input_file.close()
        output_file = open('output.txt', 'w')
        output_file.write(' '.join([str(a) for a in choose_coinciding(list1, list2)]))

    except FileNotFoundError:
        print('Specified input file does not exist')
    except ValueError:
        print('Cannot parse file content')



task_1("input.txt")
