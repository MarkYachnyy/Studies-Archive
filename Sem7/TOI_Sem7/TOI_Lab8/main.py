import numpy as np
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression, Ridge
from scipy import stats

# 1. Задание исходных данных
var1 = 2   # 1 - полином; 2 - гармонический ряд
ng = 5     # порядок полинома (ряда)
N = 100    # объем обучающей выборки
n = 1      # размерность входа
D = 1      # дисперсия ошибки
gamma = 0.01  # уровень значимости
xmin, xmax, dx = 0, 1, 0.01
xd = np.arange(xmin, xmax + dx, dx)
ld = len(xd)

# 2. Генерация обучающей выборки
XN = xmin + (xmax - xmin) * np.random.rand(N, n)
a = np.zeros((ng + 1, 1))

if var1 == 1:
    a[:] = np.array([[1], [5], [10], [0.5e3], [0.05e4], [-0.01e5]])
elif var1 == 2:
    a[:] = np.array([[0.5], [1], [10], [-5], [0.5], [-1]])

if var1 == 1:
    p = np.flip(a.ravel())
    YN = np.polyval(p, XN.ravel())[:, None] + np.sqrt(D) * np.random.randn(N, 1)
elif var1 == 2:
    YN = (
        a[0]
        + a[1] * np.sin(np.pi * XN)
        + a[2] * np.sin(2 * np.pi * XN)
        + a[3] * np.sin(3 * np.pi * XN)
        + a[4] * np.sin(4 * np.pi * XN)
        + a[5] * np.sin(5 * np.pi * XN)
        + np.sqrt(D) * np.random.randn(N, 1)
    )

# 3. Линейная регрессия через sklearn
# добавляем столбец единиц, как в MATLAB (регрессия по [1, X])
X = np.hstack((np.ones((N, 1)), XN))

# sklearn сам добавлять константу не умеет, поэтому:
lin_reg = LinearRegression(fit_intercept=False)
lin_reg.fit(X, YN)

a_ = lin_reg.coef_.reshape(-1, 1)  # (n+1, 1)
y_pred = lin_reg.predict(X)
r = YN - y_pred
RSS = float(np.sum(r ** 2))

# статистики R2, F и p-value (аналог MATLAB-кода)
mY = np.mean(y_pred)
ESS = float(np.sum((y_pred - mY) ** 2))
R2 = ESS / (RSS + ESS)

F = (ESS / n) / (RSS / (N - n - 1))
fgamma = stats.f.ppf(1 - gamma, n, N - n - 1)
p_value = 1 - stats.f.cdf(F, n, N - n - 1)

print("R^2 =", R2)
print("F =", F, ", F_crit =", fgamma)
print("p-value =", p_value)

# 4. Ridge-регрессия через sklearn (гребневая)
XtX = X.T @ X
betta = np.max(np.linalg.eigvals(XtX)) / 100

ridge = Ridge(alpha=betta, fit_intercept=False)
ridge.fit(X, YN)
a_lms = ridge.coef_.reshape(-1, 1)

y_ridge = ridge.predict(X)
Rb = float(np.sum((y_ridge - YN) ** 2))

print("Дисперсия (RSS) линейной регрессии:", RSS)
print("Дисперсия (RSS) гребневой регрессии:", Rb)

# 5. Построение регрессии и доверительных интервалов
x_grid = np.hstack((np.ones((ld, 1)), xd[:, None]))
y = x_grid @ a_
y_lms = x_grid @ a_lms

# простые доверительные интервалы по σ, как в предыдущем варианте
sigma = np.sqrt(RSS / (N - n - 1))
ymi = y - 2 * sigma
yma = y + 2 * sigma
ymin, ymax = np.min(ymi), np.max(yma)

# 6. Визуализация
plt.figure(figsize=(8, 5))
plt.grid(True)
plt.axis([xmin, xmax, ymin - 0.1, ymax + 0.1])

plt.plot(XN, YN, 'ro', label='XN-YN')
plt.plot(xd, y, '-b', label='y = f(x) (Linear)')
plt.plot(xd, y_lms, '-g', label='y = f_lms(x) (Ridge)')
plt.plot(xd, ymi, '--k', xd, yma, '--k', label='y ± 2σ')

plt.title("Полученная регрессионная зависимость", fontname='Courier')
plt.xlabel("X", fontname='Courier')
plt.ylabel("Y", fontname='Courier')

info = f'N={N}, ng={ng}, D={D}, gamma={gamma}, p-val={p_value:.4f}'
plt.text(xmin + 0.05, 0.5 * ymax, info,
         bbox=dict(facecolor='0.8', alpha=0.5), fontsize=10)

plt.legend()
plt.show()
