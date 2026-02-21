from time import perf_counter_ns
from func_parser import Parser
import tracemalloc
from root_search_methods import bissection
from root_search_methods import chord

if __name__ == '__main__':
    print("Введите функцию y = f(x). "
          "Если вы хотите взять все параметры из файла, то введите строку в формате 'file [имя файла]':")
    print("y = ", end='')
    func_string = input()
    if func_string.split()[0] == 'file':
        file = open(func_string.split()[1])
        func = Parser(file.readline()).travel()
        a, b = map(int, file.readline().split())
        e = float(file.readline())
    else:
        func = Parser(func_string).travel()
        print("Через пробел введите левую и правую границу интервала, на котором будет производиться поиск корня")
        print("!Убедитесь, что в указанном интервале содержится ровно 1 корень!")
        a, b = map(int, input().split())
        print("Введите точность поиска корня:")
        e = float(input())

    tracemalloc.start()
    start = perf_counter_ns()
    x = bissection(func, a, b, e)
    stop = perf_counter_ns() - start
    memory_used = tracemalloc.get_traced_memory()
    print(f'Методом биссекции был найден корень {x} за {stop / 1000000} миллисекунд с использованием {memory_used[1]} Байт памяти')

    tracemalloc.clear_traces()
    start = perf_counter_ns()
    x = chord(func, a, b, e)
    stop = perf_counter_ns() - start
    memory_used = tracemalloc.get_traced_memory()
    print(f'Методом хорд был найден корень {x} за {stop / 1000000} миллисекунд с использованием {memory_used[1]} Байт памяти')
    tracemalloc.clear_traces()