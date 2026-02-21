# Демидов А.Р., 9.1, Вариант 42

import itertools
import math
import numpy as np
from scipy.integrate import quad
from scipy.stats import norm
import matplotlib
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

def rayleigh_distribution_gen(sigma):
    """Генерация случайной величины, распределенной по Рэлею"""
    u = np.random.random()
    return sigma * np.sqrt(-2 * np.log(1 - u))

def pareto_distribution_gen(c, xm=1):
    """Генерация случайной величины, распределенной по Парето"""
    u = np.random.random()
    return xm / (1 - u) ** (1 / c)

def systemeqv(sigma, c):
    # Сумма распределения Рэлея и распределения Парето
    rayleigh_sample = rayleigh_distribution_gen(sigma)
    pareto_sample = pareto_distribution_gen(c)
    return rayleigh_sample + pareto_sample


def probability_gt_6(sigma, c, num_samples=10000):
    """Оценка вероятности P(Y > 6) методом Монте-Карло"""
    count = 0
    for _ in range(num_samples):
        y = systemeqv(sigma, c)
        if y > 6:
            count += 1
    return count / num_samples

matplotlib.use('TkAgg')

# задание факторов и диапазонов значений факторов
nf = 2
minf = np.array([2, 2])  # минимальные значения факторов: σ ϵ (2, 3); c ϵ (2, 3)
maxf = np.array([3, 3])  # максимальные значения факторов

# Генерация дробного двухуровневого факторного плана: уровни -1 и +1
# a, b – факторы, ab – их взаимодействие (a * b)
a = np.array([-1, +1, -1, +1])  # Столбец 'a'
b = np.array([-1, -1, +1, +1])  # Столбец 'b'
ab = a * b

fracplan = np.column_stack((a, b, ab))
# Вычисление количества экспериментов (строчки в матрице)
N = 2 ** nf

print("fracplan =")
print(fracplan)

# Фактивный фактор (столбец из единиц)
fitffact = np.ones((N, 1))
X = np.column_stack((fitffact, fracplan)).T

print("\nX =")
print(X)

# Определение данных для проведения экспериментов
fraceks = np.zeros((N, nf))
for i in range(nf):
    for j in range(N):
        fraceks[j, i] = minf[i] + (fracplan[j, i] + 1) * (maxf[i] - minf[i]) / 2

print("\nfraceks =")
print(fraceks)

# Задание параметров технического планирования
d_m = 0.1  # Доверительный интервал d = 0.1
alpha = 0.01  # Уровень значимости α = 0.01

# Определение t-критического
tkr_alpha = norm.ppf(1 - alpha / 2)

# Определение требуемого числа испытаний
NE = int(np.ceil(tkr_alpha ** 2 / (4 * d_m ** 2)))

print(f"Требуемое число испытаний NE = {NE}")

# Инициализация массива для выходных значений Y (вероятностей P(Y > 6))
Y = np.zeros(N)

# Цикл по совокупности экспериментов стратегического плана
for j in range(N):
    sigma = fraceks[j, 0]
    c = fraceks[j, 1]

    # Оценка вероятности P(Y > 6) с требуемым количеством испытаний
    Y[j] = probability_gt_6(sigma, c, NE)

# Вывод массива выходных значений Y
print("Массив выходных значений Y (P(Y > 6)) по экспериментам:", Y)

# Вычисление матрицы C = X * X'
C = X @ X.T
# Вычисление коэффициентов регрессии: b_ = inv(C) * X * Y'
b_ = np.linalg.solve(C, X @ Y)
print("Коэффициенты линейной регрессии b_ =")
print(b_)

# Формирование сетки значений факторного пространства
stepA = 0.1
stepB = 0.1
A = np.arange(minf[0], maxf[0] + 0.05, stepA)  # σ
B = np.arange(minf[1], maxf[1] + 0.05, stepB)  # c
A_grid, B_grid = np.meshgrid(A, B)

# Нормировка факторов
An = 2 * (A - minf[0]) / (maxf[0] - minf[0]) - 1
Bn = 2 * (B - minf[1]) / (maxf[1] - minf[1]) - 1
An_grid, Bn_grid = np.meshgrid(An, Bn)

# Экспериментальная поверхность реакции (вероятность P(Y > 6))
Yc = b_[0] + An_grid * b_[1] + Bn_grid * b_[2] + An_grid * Bn_grid * b_[3]

# Теоретическая поверхность реакции (аналитическая оценка)
Yo_grid = np.zeros_like(A_grid)
for i in range(A_grid.shape[0]):
    for j in range(A_grid.shape[1]):
        sigma_val = A_grid[i, j]
        c_val = B_grid[i, j]
        sigma_norm = 2 * (sigma_val - minf[0]) / (maxf[0] - minf[0]) - 1
        c_norm = 2 * (c_val - minf[1]) / (maxf[1] - minf[1]) - 1
        Yo_grid[i, j] = b_[0] + sigma_norm * b_[1] + c_norm * b_[2] + sigma_norm * c_norm * b_[3]

# Построение графиков

fig = plt.figure(figsize=(14, 6))
# Подграфик 1: Экспериментальная поверхность
ax1 = fig.add_subplot(121, projection='3d')
surf1 = ax1.plot_surface(A_grid, B_grid, Yc, cmap='viridis', edgecolor='k', alpha=0.8)
ax1.set_xlabel('σ (параметр Рэлея)')
ax1.set_ylabel('c (параметр Парето)')
ax1.set_zlabel('P(Y > 6)')
ax1.set_title('Экспериментальная поверхность реакции\n(Вероятность P(Y > 6))')
ax1.grid(True)
fig.colorbar(surf1, ax=ax1, shrink=0.5, aspect=5)

# Подграфик 2: Теоретическая поверхность
ax2 = fig.add_subplot(122, projection='3d')
surf2 = ax2.plot_surface(A_grid, B_grid, Yo_grid, cmap='plasma', edgecolor='k', alpha=0.8)
ax2.set_xlabel('σ (параметр Рэлея)')
ax2.set_ylabel('c (параметр Парето)')
ax2.set_zlabel('P(Y > 6)')
ax2.set_title('Теоретическая поверхность реакции\n(Вероятность P(Y > 6))')
ax2.grid(True)
fig.colorbar(surf2, ax=ax2, shrink=0.5, aspect=5)

plt.tight_layout()
plt.show()