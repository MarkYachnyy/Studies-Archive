import matplotlib.pyplot as plt
import numpy as np

# Параметры распределения
a = 1
b = 5

# Функция вычисления плотности
def p(x):
    return 1.0 / b * np.ones(x.shape)  # Формула из таблицы 1

N = 40000  # Число реализаций СВ
x = b * np.random.rand(N) + a  # Массив реализаций СВ

RN = np.max(x) - np.min(x)  # Вычисление размаха

h = RN / (1 + 3.2 * np.log2(N))  # Ширина интервала (формула Старджеса)
H = int(1 + np.floor(RN / h))  # Число интервалов гистограмм
X0 = np.min(x) - h / 2  # Нижняя граница
Xc = X0 + h * np.arange(1, H + 1)  # Центры интервалов

f = p(Xc)  # Теоретические значения плотности в центрах интервалов
plt.hist(x, bins=H, density=True, alpha=0.7, label='Нормированная гистограмма')

# Отрисовка плотности распределения, подписей осей и графиков
plt.plot(Xc, f, 'r-', linewidth=2, label='Плотность распределения')
plt.xlabel('x')
plt.ylabel('f(x)')
plt.title('Плотность распределения')
plt.legend()
plt.grid(True, alpha=0.3)
plt.show()
