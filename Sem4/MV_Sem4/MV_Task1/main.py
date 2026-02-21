from time import perf_counter_ns
import tracemalloc
from root_search_methods import bissection
from root_search_methods import chord
from root_search_methods import newton
import sympy

if __name__ == '__main__':
    print("Введите функцию y = f(x). "
          "Если вы хотите взять все параметры из файла, то введите строку в формате 'file [имя файла]':")
    print("y = ", end='')
    func_string = input()
    if func_string.split()[0] == 'file':
        file = open(func_string.split()[1])
        func_expr = sympy.parse_expr(file.readline())
        a, b = map(int, file.readline().split())
        e = float(file.readline())
    else:
        func_expr = sympy.parse_expr(func_string)
        print("Через пробел введите левую и правую границу интервала, на котором будет производиться поиск корня")
        print("!Убедитесь, что в указанном интервале содержится ровно 1 корень!")
        a, b = map(int, input().split())
        print("Введите точность поиска корня:")
        e = float(input())


    def func(x):
        var = sympy.Symbol('x')
        return func_expr.subs(var, x)


    tracemalloc.start()
    start = perf_counter_ns()
    res = bissection(func, a, b, e)
    x0 = res[0]
    stop = perf_counter_ns() - start
    memory_used = tracemalloc.get_traced_memory()
    print(
        f'Методом биссекции был найден корень {round(float(x0), 4)} за {stop / 1000000} миллисекунд с использованием {memory_used[1]} Байт памяти; Шагов: {res[1]}')

    tracemalloc.clear_traces()
    start = perf_counter_ns()
    x0 = chord(func, a, b, e)
    stop = perf_counter_ns() - start
    memory_used = tracemalloc.get_traced_memory()
    print(
        f'Методом хорд был найден корень {round(float(x0), 4)} за {stop / 1000000} миллисекунд с использованием {memory_used[1]} Байт памяти')

    diff_expr = sympy.diff(func_expr, sympy.Symbol('x'))

    def diff(x0):
        return diff_expr.subs(sympy.Symbol('x'), x0)

    start = perf_counter_ns()
    tracemalloc.clear_traces()
    res = newton(func, diff, a, b, e)
    x0 = res[0]
    stop = perf_counter_ns() - start
    memory_used = tracemalloc.get_traced_memory()
    print(
        f'Методом Ньютона был найден корень {round(float(x0), 4)} за {stop / 1000000} миллисекунд с использованием {memory_used[1]} Байт памяти; Шагов: {res[1]}')
    tracemalloc.clear_traces()
