import math

import matplotlib.pyplot as plt
import numpy as np


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


n = 40
x_cheb = [5 * math.cos(math.pi * (2 * k - 1) / (2 * n)) for k in range(1, n + 1)]
y_cheb = [-math.sin(math.pi * 1.5 * t) / (math.pi * 1.5 * t) for t in x_cheb]

x_2 = [x_cheb[0]]
for i in range(1, len(x_cheb)):
    x_2.append((x_cheb[i - 1] + x_cheb[i]) / 2)
    x_2.append(x_cheb[i])

y_2 = [newton(x_cheb, y_cheb, xx) for xx in x_2]

full_func_x = np.linspace(-5, 5, 200)
full_func_y = [-math.sin(math.pi * 1.5 * t) / (math.pi * 1.5 * t) for t in full_func_x]

plt.plot(full_func_x, full_func_y, linestyle='--', c='green', label='Исходная функция')
plt.plot(x_2, y_2, c='black', marker="*", label='Интерполяция Ньютона', linewidth=0.7)
plt.scatter(x_cheb, y_cheb, c='red', label='Узлы')

plt.legend()
plt.show()
