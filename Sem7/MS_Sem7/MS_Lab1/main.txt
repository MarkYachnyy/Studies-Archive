import numpy as np
from scipy.stats import norm
from scipy.integrate import quad
import matplotlib.pyplot as plt

# Имитация работы системы
def systemeqv(a, b):
    return a + b * np.sin(np.pi * (np.random.rand() - 0.5))

# Функция f(x) плотности вероятности для распределения арксинуса
def single_param_f(a, b):
    return lambda x: 1 / (np.pi * b * (1 - ((x - a)/b)**2)**0.5)

# Функция распределения арксинуса
def F(a, b, x):
    lower_lim = a - b
    return quad(single_param_f(a, b), lower_lim, x)[0]

# задание факторов и диапазонов значений факторов
nf = 2
# минимальные значения параметров
minf = np.array([3, 8])
# максимальные значения параметров
maxf = np.array([4, 9])

# Генерация проблема двухуровневого факторного плана: уровня -1 и +1
# a, b – факторы, ab – их взаимодействие (a * b)
a = np.array([-1, +1, -1, +1]) # Стройки 'a'
b = np.array([-1, -1, +1, +1]) # Стройки 'b'
ab = a * b    # Стройки 'ab'

fracplan = np.column_stack((a, b, ab)) # замена fraefact('a b ab') & MATLAB
# Вычисление количества экспериментов (строчки в матрице)
N = 2 ** nf

# Вывод матрицы факторного плана
print("fracplan =")
print(fracplan)

# Фиктивный фактор (стройки из единиц)
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

# Задание параметров тактического планирования
d_p = 0.06    # доверительный интервал (в долях сигмы)
alpha = 0.5    # уровень значимости

# Определение t-критического
tkr_alpha = norm.ppf((1 - alpha) / 2)

# Определение требуемого числа испытаний
NE = int(np.round(tkr_alpha ** 2 / (4 * d_p ** 2)))

print(f"Требуемое число испытаний NE = {NE}")

# Инициализация массива выходных значений Y
Y = np.zeros(N)

# Цикл по совокупности экспериментов стратегического плана
for j in range(N):
    a = fraceks[j, 0]
    b = fraceks[j, 1]

    s = 0 # Массив для хранения результатов испытаний

    # Цикл статистических испытаний
    for k in range(NE):
        if systemeqv(a, b) < 8:
            s += 1

    Y[j] = s / NE

# Вывод массива выходных значений Y
print("Массив выходных значений Y по экспериментам:", Y)

# Вычисление матрицы C = X * X'
C = X @ X.T

# Вычисление коэффициентов регрессии: b_ = inv(C) * X * Y'
b_ = np.linalg.solve(C, X @ Y)

print("Коэффициенты линейной регрессии b_ =")
print(b_)

# Формирование сетки значений факторного пространства
stepA=0.1 # Шаг параметра А
stepB=0.1 # Шаг параметра B
A = np.arange(minf[0], maxf[0] + 0.05, stepA) # Значения параметра А
B = np.arange(minf[1], maxf[1] + 0.05, stepB) # Значения параметра B
A_grid, B_grid = np.meshgrid(A, B)

An = 2 * (A - minf[0]) / (maxf[0] - minf[0]) - 1
Bn = 2 * (B - minf[1]) / (maxf[1] - minf[1]) - 1
An_grid, Bn_grid = np.meshgrid(An, Bn)

# Экспериментальная поверхность реакции
Yc = b_[0] + An_grid * b_[1] + Bn_grid * b_[2] + An_grid * Bn_grid * b_[3]

# Теоретическая поверхность реакции – дисперсия логнормального распределения
Yo = np.zeros(A_grid.shape)
for i in range(A_grid.shape[0]):
    for j in range(A_grid.shape[1]):
        # Вычисление через функцию распределения
        Yo[i][j] = F(A_grid[i][j], B_grid[i][j], 8)

# Построение графиков
fig = plt.figure(figsize=(14, 6))

# Подграфик 1: Экспериментальная поверхность
axl = fig.add_subplot(121, projection='3d')
axl.plot_surface(A_grid, B_grid, Yc, edgecolor='k', alpha=0.5)
axl.set_xlabel('fact a')
axl.set_ylabel('fact b')
axl.set_zlabel('Yc')
axl.set_title('Экспериментальная поверхность реакции')
axl.grid(True)

# Подграфик 2: Теоретическая поверхность
ax2 = fig.add_subplot(122, projection='3d')
ax2.plot_surface(A_grid, B_grid, Yo, edgecolor='k', alpha=0.5)
ax2.set_xlabel('fact a')
ax2.set_ylabel('fact b')
ax2.set_zlabel('Yo')
ax2.set_title('Теоретическая поверхность реакции')
ax2.grid(True)

plt.tight_layout()
plt.show()