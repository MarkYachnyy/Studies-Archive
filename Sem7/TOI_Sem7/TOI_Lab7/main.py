import numpy as np
from numpy.linalg import cholesky, det
from scipy.stats import norm
from sklearn.svm import SVC
import matplotlib.pyplot as plt

np.random.seed(42)

# randncor, как вы дали (чуть подправлен импорт)
def randncor(n, N, C):
    try:
        A = cholesky(C).T
        m = n
    except np.linalg.LinAlgError:
        m = n - 1
        # Для простоты, в случае ошибки используем усеченную матрицу
        A = cholesky(C[:m, :m]).T

    # генерация матрицы реализаций m*N гауссовских независимых случайных величин
    u = np.random.randn(m, N)

    # получение матрицы реализаций N гауссовских коррелированных векторов размерности m
    x = A @ u
    return x, m


# ======================= 1. Задание исходных данных =======================

n = 2      # размерность признакового пространства
M = 4      # число классов
K = 300    # количество статистических испытаний

dm = 2.0   # расстояние между математическими ожиданиями по осям (не используется далее явно)
C = np.zeros((n, n, M))
C_ = np.zeros_like(C)

pw = np.array([1, 1, 1,1], dtype=float)
pw = pw / pw.sum()

m_means = np.array([[2, 10],
                    [12, -2],
                    [-15, 5],
                    [-4, 0]], dtype=float).T    # shape (n, M)

C[:, :, 0] = np.array([[4, -2],
                       [-2, 4]], dtype=float)
C[:, :, 1] = np.array([[4, -2],
                       [-2, 4]], dtype=float)
C[:, :, 2] = np.array([[5, 3],
                       [3, 5]], dtype=float)
C[:, :, 3] = np.array([[5, 3],
                       [3, 5]], dtype=float)

for k in range(M):
    C_[:, :, k] = np.linalg.inv(C[:, :, k])

np_sum_pw = pw.sum()
pw = pw / np_sum_pw   # на всякий случай нормировка


# ================== 2. Обучение SVM-классификаторов ======================

# 2.1. Генерация обучающих выборок
Ks = np.floor(K * pw).astype(int)
Ks[-1] = K - Ks[:-1].sum()

XN = []      # список массивов (n, Ks[i]) для каждого класса
X_list = []  # общая обучающая выборка
Y_list = []  # номера классов

for i in range(M):
    Xi, _ = randncor(n, Ks[i], C[:, :, i])
    Xi = Xi + m_means[:, i].reshape(-1, 1)
    XN.append(Xi)
    X_list.append(Xi.T)  # (Ks[i], n)
    Y_list.append(np.full((Ks[i],), i + 1, dtype=int))  # классы с 1

X = np.vstack(X_list)          # shape (K, n)
Y = np.concatenate(Y_list)     # shape (K,)


# 2.2. Обучаем SVM для каждой пары классов (one-vs-one вручную)
r = 1.0   # BoxConstraint

svm_strs = [[None for _ in range(M)] for _ in range(M)]

for i in range(M - 1):
    for j in range(i + 1, M):
        # Формирование смешанной выборки
        Xi = XN[i].T   # (Ks[i], n)
        Xj = XN[j].T   # (Ks[j], n)
        Xij = np.vstack([Xi, Xj])

        # Метки: True для i-го класса, False для j-го
        D_ij = np.concatenate([
            np.ones(Ks[i], dtype=bool),
            np.zeros(Ks[j], dtype=bool)
        ])

        # Вариант с rbf ядром (как в MATLAB-коде)
        clf = SVC(
            kernel='linear',
            C=r,
            gamma=1.0,    # KernelScale=1 примерно соответствует gamma=1
            probability=False
        )
        clf.fit(Xij, D_ij)
        svm_strs[i][j] = clf


# ================== 2.3. Визуализация областей классов ===================

show = True
if show and n == 2:
    d = 0.05
    x1_min, x1_max = X[:, 0].min(), X[:, 0].max()
    x2_min, x2_max = X[:, 1].min(), X[:, 1].max()
    x1_grid, x2_grid = np.meshgrid(
        np.arange(x1_min, x1_max + d, d),
        np.arange(x2_min, x2_max + d, d)
    )
    xGrid = np.c_[x1_grid.ravel(), x2_grid.ravel()]
    N_grid = xGrid.shape[0]

    idxs = []   # список N_grid-вектором для каждой пары
    sv_all = [] # опорные векторы всех классификаторов

    for i in range(M - 1):
        for j in range(i + 1, M):
            clf = svm_strs[i][j]
            sv_all.append(clf.support_vectors_)
            cls = clf.predict(xGrid)   # bool
            idx = (i + 1) * cls + (j + 1) * (~cls)  # индексы классов (1..M)
            idxs.append(idx.reshape(-1, 1))

    idxs = np.hstack(idxs)            # (N_grid, num_pairs)
    # голосование по моде
    from scipy import stats
    iai, _ = stats.mode(idxs, axis=1, keepdims=False)
    iai = iai.astype(int)

    # отрисовка
    plt.figure()
    if M == 2:
        colors_regions = np.array([[0.5, 0.1, 0.5],
                                   [0.1, 0.5, 0.5]])
    else:
        colors_regions = np.array([[255, 125, 125],
                                   [255, 199, 102],
                                   [82, 148, 255],
                                   [160, 232, 160]])
        colors_classes = np.array([[255, 0, 0],
                                   [255, 165, 0],
                                   [0, 0, 255],
                                   [0, 255, 0]])

    for cls_id in range(1, M + 1):
        mask = (iai == cls_id)
        plt.scatter(
            xGrid[mask, 0], xGrid[mask, 1],
            s=5,
            color=colors_regions[cls_id - 1]/255,
            alpha=0.3,
            label=f'class {cls_id} region'
        )

    # обучающие выборки
    for cls_id in range(1, M + 1):
        mask = (Y == cls_id)
        plt.scatter(
            X[mask, 0], X[mask, 1],
            edgecolor='k',
            color=colors_classes[cls_id-1]/255,
            label=f'class {cls_id}'
        )

    # опорные векторы
    sv_all = np.vstack(sv_all)
    plt.scatter(
        sv_all[:, 0], sv_all[:, 1],
        s=80, facecolors='none', edgecolors='k',
        label='Support Vector'
    )

    plt.legend()
    plt.tight_layout()
    plt.show()


# ====== 3. Теоретические матрицы вероятностей ошибок (PIJ, PIJB) ========

# PIJ = np.zeros((M, M))
# PIJB = np.zeros((M, M))
# l0_ = np.zeros((M, M))
#
# for i in range(M):
#     for j in range(i + 1, M):
#         dmij = m_means[:, i] - m_means[:, j]
#         l0_[i, j] = np.log(pw[j] / pw[i])
#         dti = det(C[:, :, i])
#         dtj = det(C[:, :, j])
#
#         trij = np.trace(C_[:, :, j] @ C[:, :, i] - np.eye(n))
#         trji = np.trace(np.eye(n) - C_[:, :, i] @ C[:, :, j])
#
#         mg1 = 0.5 * (trij + dmij.T @ C_[:, :, j] @ dmij - np.log(dti / dtj))
#         Dg1 = 0.5 * (trij ** 2) + dmij.T @ C_[:, :, j] @ C[:, :, i] @ C_[:, :, j] @ dmij
#
#         mg2 = 0.5 * (trji - dmij.T @ C_[:, :, i] @ dmij + np.log(dtj / dti))
#         Dg2 = 0.5 * (trji ** 2) + dmij.T @ C_[:, :, i] @ C[:, :, j] @ C_[:, :, i] @ dmij
#
#         sD1 = np.sqrt(Dg1)
#         sD2 = np.sqrt(Dg2)
#
#         PIJ[i, j] = norm.cdf(l0_[i, j], loc=mg1, scale=sD1)
#         PIJ[j, i] = 1 - norm.cdf(l0_[i, j], loc=mg2, scale=sD2)
#
#         mu2 = (1 / 8) * dmij.T @ np.linalg.inv((C[:, :, i] / 2 + C[:, :, j] / 2)) @ dmij \
#               + 0.5 * np.log((dti + dtj) / (2 * np.sqrt(dti * dtj)))
#
#         PIJB[i, j] = np.sqrt(pw[j] / pw[i]) * np.exp(-mu2)
#         PIJB[j, i] = np.sqrt(pw[i] / pw[j]) * np.exp(-mu2)
#
# for i in range(M):
#     PIJB[i, i] = 1 - PIJB[i, :].sum()
#     PIJ[i, i] = 1 - PIJ[i, :].sum()


# ========== 4. Тестирование алгоритма методом статистических испытаний ==========

Pcv = np.zeros((M, M))   # экспериментальная матрица по SVM
# Pc_ = np.zeros((M, M))   # экспериментальная матрица по теор. ГСВ

for k_iter in range(K):
    for i in range(M):
        x, _ = randncor(n, 1, C[:, :, i])
        x = x + m_means[:, i].reshape(-1, 1)   # shape (n, 1)
        x_vec = x[:, 0]                        # (n,)

        # # ГСВ-классификатор (из 3-й лабы)
        # u = np.zeros(M)
        # for j in range(M):
        #     diff = x_vec - m_means[:, j]
        #     u[j] = -0.5 * diff.T @ C_[:, :, j] @ diff - 0.5 * np.log(det(C[:, :, j])) + np.log(pw[j])
        # iai = np.argmax(u)      # индекс 0..M-1
        # Pc_[i, iai] += 1

        # Прогон по всем SVM-классификаторам
        iais = []
        for ii in range(M - 1):
            for jj in range(ii + 1, M):
                clf = svm_strs[ii][jj]
                cl = clf.predict(x_vec.reshape(1, -1))[0]  # bool
                if cl:
                    iai_ = ii + 1
                else:
                    iai_ = jj + 1
                iais.append(iai_)

        # голосование
        iais = np.array(iais)
        iai_vote, _ = stats.mode(iais, keepdims=False)
        iai_vote = int(iai_vote) - 1   # в индексы 0..M-1
        Pcv[i, iai_vote] += 1

# Pc_ = Pc_ / K
Pcv = Pcv / K

# print('Теоретическая матрица вероятностей ошибок')
# print(PIJ)
# print('Матрица вероятностей ошибок на основе границы Чернова')
# print(PIJB)
# print('Экспериментальная матрица вероятностей ошибок (ГСВ)')
# print(Pc_)
print('Экспериментальная матрица вероятностей ошибок SVM')
print(Pcv)
