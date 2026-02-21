import numpy as np
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression
from scipy import stats

# 1. Задание исходных данных
var1 = 2  # вид восстанавливаемой функции (1-полином; 2-гармонический ряд)
ng = 5    # порядок полинома (ряда), ng+1 - количество коэффициентов
N = 1000   # объем обучающей выборки
n = 1     # размерность вектора входных переменных (фиксирована)
D = 2    # дисперсия ошибки измерения выходной переменной
gamma = 0.03  # уровень значимости для проверки гипотез по критерию Фишера
xmin, xmax, dx = 0, 1, 0.01
xd = np.arange(xmin, xmax + dx, dx)
ld = len(xd)

# 2. Генерация обучающей выборки данных
XN = xmin + (xmax - xmin) * np.random.rand(N, n)
YN = np.zeros((N, 1))
a = np.zeros(ng + 1)

if var1 == 1:
    # исходные данные для полинома
    a = np.array([2, -3, 17, 0.5*10**3, 0.05*10**4, -0.01*10**5])

if var1 == 2:
    # исходные данные для гармонического ряда
    a = np.array([2, -3, 17, 5, 2, -1])

# Задание аппроксимируемой функции
if var1 == 1:
    p = np.flip(a)  # в обратном порядке
    YN = np.polyval(p, XN)[:, None] + np.sqrt(D) * np.random.randn(N, 1)

if var1 == 2:
    YN = (a[0] * np.ones((N, 1)) +
          a[1] * np.sin(np.pi * XN) +
          a[2] * np.sin(2 * np.pi * XN) +
          a[3] * np.sin(3 * np.pi * XN) +
          a[4] * np.sin(4 * np.pi * XN) +
          a[5] * np.sin(5 * np.pi * XN) +
          np.sqrt(D) * np.random.randn(N, 1))

# 3. Обращение к функции вычисления коэффициентов регрессии (аналог regress)
X = np.hstack([np.ones((N, 1)), XN])

# Используем LinearRegression + ручной расчет доверительных интервалов
lin_reg = LinearRegression(fit_intercept=False)
lin_reg.fit(X, YN.ravel())
a_ = lin_reg.coef_[:, None]  # коэффициенты (n+1)x1

# Остатки
r = YN.ravel() - lin_reg.predict(X)
RSS = np.sum(r**2)

# Статистики (аналог stat из regress)
mY = np.mean(X @ a_)
ESS = np.sum((X @ a_ - mY)**2)
R2 = ESS / (RSS + ESS)
F = (ESS / n) / (RSS / (N - n - 1))
fgamma = stats.f.ppf(1 - gamma, n, N - n - 1)
p_value = 1 - stats.f.cdf(F, n, N - n - 1)

# Вывод статистик (аналог disp)
print('Вычисляемые статистики: R^2, F, p_value, RSS/(N-n-1)')
print(R2, F, p_value, RSS / (N - n - 1))

# 4. Построение регрессии и границ восстанавливаемой функции
x = np.hstack([np.ones((ld, 1)), xd[:, None]])
y = x @ a_

# Доверительные интервалы для предсказаний (аналог aint)
sigma = np.sqrt(RSS / (N - n - 1))
# Матрица Хатчинсона для доверительных интервалов
XtX_inv = np.linalg.inv(X.T @ X)
H = x @ XtX_inv @ x.T
se_pred = sigma * np.sqrt(np.diag(H))
t_crit = stats.t.ppf(1 - gamma / 2, N - n - 1)
ymi = y - t_crit * se_pred[:, None]
yma = y + t_crit * se_pred[:, None]
ymin = np.min(ymi)
ymax = np.max(yma)

# 5. Визуализация результатов
plt.figure(1, figsize=(10, 6))
plt.rcParams.update({'font.size': 12})
plt.grid(True)
plt.axis([xmin, xmax, ymin - 0.1, ymax + 0.1])

# Построение графиков
plt.plot(XN, YN, 'ro', linewidth=1.25, label='XN-YN')
plt.plot(xd, y, '-b', linewidth=1.25, label='y=f(x)')
plt.plot(xd, ymi.ravel(), '--k', linewidth=1.25, label='y-dy')
plt.plot(xd, yma.ravel(), '--k', linewidth=1.25, label='y+dy')

plt.title('Полученная регрессионная зависимость', fontname='Courier', fontsize=14)
plt.xlabel('X', fontname='Courier', fontsize=12)
plt.ylabel('Y', fontname='Courier', fontsize=12)

# Текст с параметрами (аналог text в MATLAB)
info_str = f'N={N} ng={ng} D={D} gamma={gamma} p-val={p_value:.4f}'
plt.text(xmin + 0.1, 0.5 * ymax, info_str,
         horizontalalignment='left',
         bbox=dict(facecolor=[0.8, 0.8, 0.8], alpha=0.8),
         fontsize=12)

plt.legend()
plt.tight_layout()
plt.show()
