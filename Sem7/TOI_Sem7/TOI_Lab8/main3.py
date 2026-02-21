import numpy as np
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression
from scipy import stats

# 1. Задание исходных данных
var1 = 2      # 1 - полином; 2 - гармонический ряд
ng = 5        # порядок ряда
N = 100       # объем обучающей выборки
n = 1         # размерность входного вектора
D = 1         # дисперсия шума
gamma = 0.01  # уровень значимости Фишера

xmin, xmax, dx = 0, 1, 0.01
xd = np.arange(xmin, xmax + dx, dx)
ld = len(xd)

# 2. Генерация обучающей выборки
XN = xmin + (xmax - xmin) * np.random.rand(N, n)
YN = np.zeros((N, 1))
a = np.zeros((ng + 1, 1))

if var1 == 1:
    # исходные данные для полинома
    a[:] = np.array([[1], [5], [10], [0.5 * 10**3], [0.05 * 10**4], [-0.01 * 10**5]])

if var1 == 2:
    # исходные данные для гармонического ряда (изменённые коэффициенты)
    a[:] = np.array([[2], [-3], [17], [5], [2], [-1]])

# Задание аппроксимируемой функции
if var1 == 1:
    p = np.flip(a.ravel())
    YN = np.polyval(p, XN.ravel())[:, None] + np.sqrt(D) * np.random.randn(N, 1)

if var1 == 2:
    YN = (
        a[0] * np.ones((N, 1))
        + a[1] * np.sin(np.pi * XN)
        + a[2] * np.sin(2 * np.pi * XN)
        + a[3] * np.sin(3 * np.pi * XN)
        + a[4] * np.sin(4 * np.pi * XN)
        + a[5] * np.sin(5 * np.pi * XN)
        + np.sqrt(D) * np.random.randn(N, 1)
    )

# 3. Линейная регрессия (аналог regress(YN, X, gamma))
X = np.hstack((np.ones((N, 1)), XN))

# без автоматического интерсепта, т.к. единицы уже в X
lin_reg = LinearRegression(fit_intercept=False)
lin_reg.fit(X, YN)

a_ = lin_reg.coef_.reshape(-1, 1)  # (n+1,1)
y_hat = X @ a_
r = YN - y_hat                      # остатки

RSS = float(np.sum(r**2))
mY = float(np.mean(y_hat))
ESS = float(np.sum((y_hat - mY) ** 2))
R2 = ESS / (RSS + ESS)

F = (ESS / n) / (RSS / (N - n - 1))
fgamma = stats.f.ppf(1 - gamma, n, N - n - 1)
p_value = 1 - stats.f.cdf(F, n, N - n - 1)

print('Вычисляемые статистики: R^2, F, p_value, RSS/(N-n-1)')
print([R2, F, p_value, RSS / (N - n - 1)])

# 3.1. Доверительные интервалы для коэффициентов (аналог aint)
XtX_inv = np.linalg.inv(X.T @ X)
sigma2 = RSS / (N - n - 1)
se_beta = np.sqrt(np.diag(sigma2 * XtX_inv))          # стандартные ошибки
t_crit = stats.t.ppf(1 - gamma / 2, N - n - 1)
# интервальные оценки коэффициентов (aint: (n+1)x2)
aint = np.vstack((a_.ravel() - t_crit * se_beta,
                  a_.ravel() + t_crit * se_beta)).T

# 4. Построение регрессии и границ восстанавливаемой функции
x = np.hstack((np.ones((ld, 1)), xd[:, None]))
y = x @ a_

# интервалы для предсказаний, аналог ymi/yma = x * aint(:,1/2)
ymi = x @ aint[:, 0:1]
yma = x @ aint[:, 1:2]
ymin = float(np.min(ymi))
ymax = float(np.max(yma))

# 5. Визуализация результатов
plt.figure(1, figsize=(8, 5))
plt.grid(True)
plt.axis([xmin, xmax, ymin - 0.1, ymax + 0.1])

plt.plot(XN, YN, 'ro', linewidth=1.25, label='XN-YN')
plt.plot(xd, y, '-b', linewidth=1.25, label='y=f(x)')
plt.plot(xd, ymi.ravel(), '--k', linewidth=1.25, label='y-dy')
plt.plot(xd, yma.ravel(), '--k', linewidth=1.25, label='y+dy')

plt.title('Полученная регрессионная зависимость', fontname='Courier')
plt.xlabel('X', fontname='Courier')
plt.ylabel('Y', fontname='Courier')

info_str = f'N={N} ng={ng} D={D} gamma={gamma} p-val={p_value:.4f}'
plt.text(xmin + 0.1, 0.5 * ymax, info_str,
         horizontalalignment='left',
         bbox=dict(facecolor=[0.8, 0.8, 0.8], alpha=0.8),
         fontsize=12)

plt.legend()
plt.tight_layout()
plt.show()
