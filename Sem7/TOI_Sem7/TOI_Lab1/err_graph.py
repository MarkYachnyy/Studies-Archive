import matplotlib.pyplot as plt
import numpy as np

# Функция вычисления плотности в точке
def p(x):
    return 1.0 / b * np.ones(x.shape)

# Функция вычисления значений плотности для гистограммы
def calculate_density(data, bins_count=10):
    N = len(data)
    min_val, max_val = np.min(data), np.max(data)
    RN = max_val - min_val
    bin_size = RN / bins_count
    max_val += 1e-10
    bin_edges = np.linspace(min_val, max_val, bins_count + 1)
    counts = np.zeros(bins_count, dtype=int)

    for i in range(bins_count):
        left_edge = bin_edges[i]
        right_edge = bin_edges[i + 1]

        contains = (data >= left_edge) & (data < right_edge)

        counts[i] = np.sum(contains)

    densities = counts / (N * bin_size)
    return densities, bin_edges[:-1] + bin_size / 2

# Параметры распределения
a = 1
b = 5

Ns = []
errs = []
for i in range(1, 201):
    N = i * 1000
    x = a + b * np.random.rand(N)  # массив реализаций СВ
    RN = np.max(x) - np.min(x)  # размах значений СВ
    h = RN / (1 + 3.2 * np.log2(N))  # ширина интервала
    H = int(1 + np.floor(RN / h))  # число интервалов гистограмм
    Ns.append(N)
    density, middles = calculate_density(x, H)
    errs.append(np.mean(np.abs(density - p(middles))))

plt.plot(Ns, errs, label='Средняя ошибка\nплотности вероятности')
plt.xlabel("Число реализаций СВ")
plt.legend()
plt.show()
