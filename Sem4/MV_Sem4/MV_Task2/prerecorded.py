import math

import matplotlib.pyplot as plt
import numpy as np


def read_csv(filename: str):
    X = []
    Y = []
    for line in [_ for _ in open(filename)][1:]:
        args = line.split(';')
        if len(args) != 2:
            raise ValueError
        X.append(float(args[0]))
        Y.append(float(args[1]))
    return X, Y


def newton(X, Y, t):
    res = 0
    for k in range(0, len(X)):
        a = finite_residual(X, Y, 0, k)
        for m in range(0, k):
            a *= (t - X[m])
        res += a
    return res


def finite_residual(X, Y, i, k):
    res = 0
    for m in range(i, i + k + 1):
        a = Y[m]
        for j in range(i, i + k + 1):
            if j == m:
                continue
            a /= (X[m] - X[j])
        res += a
    return res


x, y = read_csv("./data.csv")

x_2 = np.linspace(min(x), max(x), len(x) * 2 - 1)
y_2 = [newton(x, y, xx) for xx in x_2]

full_func_x = np.linspace(-5, 5, 200)
full_func_y = [-math.sin(math.pi * 1.5 * t) / (math.pi * 1.5 * t) for t in full_func_x]

plt.plot(full_func_x, full_func_y, linestyle='--', c = 'green', label='Исходная функция')
plt.plot(x_2, y_2, c='black', marker="*", label='Интерполяция Ньютона', linewidth=0.7)
plt.scatter(x, y, c='red', label='Узлы')

plt.legend()
plt.show()
