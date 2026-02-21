import numpy as np
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression
from scipy import stats

np.random.seed(67)

# 1. Задание исходных данных
var1 = 2   # 1 - полином; 2 - гармонический ряд
ng = 5
n = 1
D = 2
gamma = 0.03
xmin, xmax, dx = 0, 1, 0.01
xd = np.arange(xmin, xmax + dx, dx)
ld = len(xd)

# истинные коэффициенты
if var1 == 1:
    a_true = np.array([2, -3, 17, 0.5*10**3, 0.05*10**4, -0.01*10**5])
else:
    a_true = np.array([2, -3, 17, 5, 2, -1])

# сетка для регрессии
x_grid = np.hstack([np.ones((ld, 1)), xd[:, None]])

# объёмы выборки
Ns = [50, 100, 200, 1000]

# массивы для статистик
R2_list = []
F_list = []
p_list = []
RSSn_list = []

for N in Ns:
    # 2. Генерация обучающей выборки
    XN = xmin + (xmax - xmin) * np.random.rand(N, n)
    YN = np.zeros((N, 1))

    if var1 == 1:
        p = np.flip(a_true)
        YN = np.polyval(p, XN)[:, None] + np.sqrt(D) * np.random.randn(N, 1)
    else:
        YN = (a_true[0] * np.ones((N, 1)) +
              a_true[1] * np.sin(np.pi * XN) +
              a_true[2] * np.sin(2 * np.pi * XN) +
              a_true[3] * np.sin(3 * np.pi * XN) +
              a_true[4] * np.sin(4 * np.pi * XN) +
              a_true[5] * np.sin(5 * np.pi * XN) +
              np.sqrt(D) * np.random.randn(N, 1))

    # 3. Линейная регрессия
    X = np.hstack([np.ones((N, 1)), XN])
    lin_reg = LinearRegression(fit_intercept=False)
    lin_reg.fit(X, YN.ravel())
    a_ = lin_reg.coef_[:, None]

    r = YN.ravel() - lin_reg.predict(X)
    RSS = np.sum(r**2)

    mY = np.mean(X @ a_)
    ESS = np.sum((X @ a_ - mY)**2)
    R2 = ESS / (RSS + ESS)
    F = (ESS / n) / (RSS / (N - n - 1))
    fgamma = stats.f.ppf(1 - gamma, n, N - n - 1)
    p_value = 1 - stats.f.cdf(F, n, N - n - 1)

    R2_list.append(R2)
    F_list.append(F)
    p_list.append(p_value)
    RSSn_list.append(RSS / (N - n - 1))

    # 4. Регрессия и доверительные интервалы на сетке
    y = x_grid @ a_

    sigma = np.sqrt(RSS / (N - n - 1))
    XtX_inv = np.linalg.inv(X.T @ X)
    H = x_grid @ XtX_inv @ x_grid.T
    se_pred = sigma * np.sqrt(np.diag(H))
    t_crit = stats.t.ppf(1 - gamma / 2, N - n - 1)
    ymi = y - t_crit * se_pred[:, None]
    yma = y + t_crit * se_pred[:, None]
    ymin = np.min(ymi)
    ymax = np.max(yma)

    # 5. График для данного N
    if N == 200:
        plt.figure(figsize=(8, 5))
        plt.grid(True)
        plt.axis([xmin, xmax, ymin - 0.1, ymax + 0.1])

        plt.plot(XN, YN, 'ro', linewidth=1.25, label='XN-YN')
        plt.plot(xd, y, '-b', linewidth=1.25, label='y=f(x)')
        plt.plot(xd, ymi.ravel(), '--k', linewidth=1.25, label='y-dy')
        plt.plot(xd, yma.ravel(), '--k', linewidth=1.25, label='y+dy')

        plt.title(f'Регрессия для N={N}')
        plt.xlabel('X')
        plt.ylabel('Y')

        info_str = f'N={N} ng={ng} D={D} gamma={gamma} p-val={p_value:.4f}'
        plt.text(xmin + 0.05, 0.5 * ymax, info_str,
                 horizontalalignment='left',
                 bbox=dict(facecolor=[0.8, 0.8, 0.8], alpha=0.8),
                 fontsize=10)

        plt.legend()
        plt.tight_layout()

# 6. Графики зависимостей статистик от N

plt.figure(figsize=(7, 4))
plt.grid(True)
plt.plot(Ns, R2_list, '-o')
plt.xlabel('N')
plt.ylabel('R²')
plt.title('Зависимость R² от N')
plt.tight_layout()

plt.figure(figsize=(7, 4))
plt.grid(True)
plt.plot(Ns, F_list, '-o')
plt.xlabel('N')
plt.ylabel('F')
plt.title('Зависимость F от N')
plt.tight_layout()

plt.figure(figsize=(7, 4))
plt.grid(True)
plt.plot(Ns, p_list, '-o')
plt.xlabel('N')
plt.ylabel('p-value')
plt.title('Зависимость p-value от N')
plt.tight_layout()

plt.figure(figsize=(7, 4))
plt.grid(True)
plt.plot(Ns, RSSn_list, '-o')
plt.xlabel('N')
plt.ylabel('RSS / (N - n - 1)')
plt.title('Зависимость RSS/(N-n-1) от N')
plt.tight_layout()

plt.show()