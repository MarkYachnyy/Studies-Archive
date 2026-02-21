import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import norm, multivariate_normal
from scipy.linalg import cholesky, inv


def randncor(n, N, C):
    try:
        A = cholesky(C)
        m = n
    except:
        m = np.linalg.matrix_rank(C) - 1

    u = np.random.randn(m, N)
    x = A.T @ u
    return x, m


n = 2
M = 2
K = 1000

m = np.array([[2, 1], [-3, 2]])
pw = np.array([2, 1])
np_sum = np.sum(pw)
pw = pw / np_sum

C = np.array([[4, -2], [-2, 4]])
C_ = inv(C)

Ks = np.floor(pw * K).astype(int)
Ks[-1] = K - np.sum(Ks[:-1])

label = ['bo', 'r+', 'k*', 'gx']
IMS = np.array([]).reshape(2, 0)

plt.figure()
plt.title('Исходные метки образов')

for i in range(M):
    ims_temp, _ = randncor(n, Ks[i], C)
    ims = m[:, i:i + 1] + ims_temp
    plt.plot(ims[0, :], ims[1, :], label[i], markersize=5, linewidth=1)
    IMS = np.hstack((IMS, ims))

plt.legend(['Класс 1', 'Класс 2', 'Класс 3'])
plt.grid(True)

G = np.zeros((M, n + 1))
PIJ = np.zeros((M, M))
l0_ = np.zeros((M, M))

for i in range(M):
    G[i, 0:n] = (C_ @ m[:, i]).T
    G[i, n] = -0.5 * m[:, i].T @ C_ @ m[:, i]

    for j in range(i + 1, M):
        l0_[i, j] = np.log(pw[j] / pw[i])
        h = 0.5 * (m[:, i] - m[:, j]).T @ C_ @ (m[:, i] - m[:, j])
        sD = np.sqrt(2 * h)
        PIJ[i, j] = norm.cdf(l0_[i, j], loc=h, scale=sD)
        PIJ[j, i] = 1 - norm.cdf(l0_[i, j], loc=-h, scale=sD)

    PIJ[i, i] = 1 - np.sum(PIJ[i, :])

plt.figure()
plt.title('Результат классификации образов')

for i in range(K):
    z = np.concatenate((IMS[:, i], [1]))
    u = G @ z + np.log(pw)
    iai = np.argmax(u)
    plt.plot(IMS[0, i], IMS[1, i], label[iai], markersize=5, linewidth=1)

plt.legend(['Класс 1', 'Класс 2', 'Класс 3'])
plt.grid(True)

Pc_ = np.zeros((M, M))

for k in range(K):
    for i in range(M):
        x_temp, _ = randncor(n, 1, C)
        x = m[:, i:i + 1] + x_temp
        x_ext = np.concatenate((x.flatten(), [1]))
        u = G @ x_ext + np.log(pw)
        iai = np.argmax(u)
        Pc_[i, iai] += 1

Pc_ = Pc_ / K

print('Теоретическая матрица вероятностей ошибок:')
print(PIJ)
print('Экспериментальная матрица вероятностей ошибок:')
print(Pc_)

if n == 2:
    D = 1
    xmin1 = -4 * np.sqrt(D) + np.min(m[0, :])
    xmax1 = 4 * np.sqrt(D) + np.max(m[0, :])
    xmin2 = -4 * np.sqrt(D) + np.min(m[1, :])
    xmax2 = 4 * np.sqrt(D) + np.max(m[1, :])
    x1 = np.arange(xmin1, xmax1, 0.05)
    x2 = np.arange(xmin2, xmax2, 0.05)
    X1, X2 = np.meshgrid(x1, x2)
    x12 = np.column_stack((X1.ravel(), X2.ravel()))

    plt.figure(figsize=(10, 8))
    plt.title('Области локализации классов и разделяющие границы')
    plt.xlabel('x1')
    plt.ylabel('x2')
    plt.grid(True)
    plt.axis([xmin1, xmax1, xmin2, xmax2])

    for i in range(M):
        f2 = multivariate_normal.pdf(x12, mean=m[:, i], cov=C)
        f3 = f2.reshape(X1.shape)
        contour_levels = [0.01, 0.5 * np.max(f3)]
        CS = plt.contour(X1, X2, f3, levels=contour_levels, colors='b', linewidths=0.75)
        plt.clabel(CS, inline=True, fontsize=8)

        for j in range(i + 1, M):
            wij = C_ @ (m[:, i] - m[:, j])
            wij0 = -0.5 * (m[:, i] + m[:, j]).T @ C_ @ (m[:, i] - m[:, j])
            f4 = x12 @ wij + wij0
            f5 = f4.reshape(X1.shape)
            CS_ = plt.contour(X1, X2, f5, levels=[l0_[i, j] + 0.0001], colors='k', linewidths=1.25)

    strv1 = 'pw='
    strv2 = np.array2string(pw, formatter={'float_kind': lambda x: "%.2f" % x})
    plt.text(xmin1 + 1, xmax2 - 1, f'{strv1} {strv2}',
             horizontalalignment='left',
             backgroundcolor=[0.8, 0.8, 0.8],
             fontsize=12)

    plt.legend(['wi', 'gij(x)=0'])
    plt.tight_layout()

plt.show()