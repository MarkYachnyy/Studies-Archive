import matplotlib.pyplot as plt
import numpy as np


# Функция сглаживания графика алгоритмом скользящего среднего
def moving_average(data, window_size: int = 3):
    smoothed = np.zeros(len(data))

    for i in range(len(smoothed) - window_size + 1):
        smoothed[i] = np.mean(data[i:i + window_size])

    for i in range(1, window_size):
        smoothed[-1] = smoothed[-window_size]

    return smoothed


# Параметры распределения
a = 1.0
b = 5

m = a + b / 2.0  # теоретическое мат. ожидание
d = b * b / 12.0  # теоретическая дисперсия

Ns = []  # массив количеств реализаций СВ
Merrs = []  # массив ошибок вычисления мат. ожидания
Derrs = []  # массив ошибок вычисления дисперсии

for i in range(1, 201):
    Ns.append(i * 1000)
    x = b * np.random.rand(i * 1000) + a
    M = np.mean(x)
    D = np.var(x)
    Merrs.append(abs(M - m))  # Вычисление абсолютной разности теоретического и эмпирического значений мат. ожидания
    Derrs.append(abs(D - d))  # Вычисление абсолютной разности теоретического и эмпирического значений дисперсии

# Отображение получившихся данных
plt.plot(Ns, moving_average(Merrs, 4), label='Ошибка мат. ожидания')
plt.plot(Ns, moving_average(Derrs, 4), label='Ошибка дисперсии')
plt.legend()
plt.xlabel("Число реализаций СВ")
plt.show()
