import numpy as np
from numpy.linalg import cholesky, det, inv
from scipy.stats import norm
from scipy import stats
from sklearn.svm import SVC
import matplotlib.pyplot as plt


# randncor функция (как в предыдущем)
def randncor(n, N, C):
    try:
        A = cholesky(C).T
        m = n
    except np.linalg.LinAlgError:
        m = n - 1
        A = cholesky(C[:m, :m]).T

    u = np.random.randn(m, N)
    x = A @ u
    return x, m


# ======================= 1. Задание исходных данных =======================

np.random.seed(42)

n = 2  # размерность признакового пространства
M = 4  # число классов
K = 300  # количество статистических испытаний

C = np.zeros((n, n, M))
C_ = np.zeros_like(C)

pw = np.array([0.1, 0.1, 0.1, 0.1], dtype=float)
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
    C_[:, :, k] = inv(C[:, :, k])

# ================== 2. Обучение SVM-классификаторов ======================

# 2.1. Генерация обучающих выборок
Ks = np.floor(K * pw).astype(int)
Ks[-1] = K - Ks[:-1].sum()

XN = []  # список массивов (n, Ks[i]) для каждого класса
X_list = []  # общая обучающая выборка (K, n)
Y_list = []  # номера классов

for i in range(M):
    Xi, _ = randncor(n, Ks[i], C[:, :, i])
    Xi = Xi + m_means[:, i].reshape(-1, 1)
    XN.append(Xi)
    X_list.append(Xi.T)
    Y_list.append(np.full((Ks[i],), i + 1, dtype=int))

X = np.vstack(X_list)  # shape (K, n)
Y = np.concatenate(Y_list)  # shape (K,)

# 2.2. Обучаем SVM для каждого класса против остальных (one-vs-rest)
r = 1.0  # BoxConstraint

svm_strs = [None] * M

for i in range(M):
    # Метки: True для i-го класса, False для остальных
    D_i = (Y == (i + 1))

    clf = SVC(
        kernel='linear',
        C=r,
        gamma=1.0,  # KernelScale=1
        probability=True  # Нужны вероятности/score
    )
    clf.fit(X, D_i)
    svm_strs[i] = clf

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

    Scores = []  # апостериорные вероятности для каждого класса
    sv_all = []  # опорные векторы всех классификаторов

    # Классифицируем сетку каждым one-vs-rest классификатором
    for i in range(M):
        clf = svm_strs[i]
        sv_all.append(clf.support_vectors_)
        # predict_proba[:, 1] - вероятность принадлежности к i-му классу
        score = clf.predict_proba(xGrid)[:, 1]
        Scores.append(score)

    Scores = np.column_stack(Scores)  # (N_grid, M)
    maxScore = np.argmax(Scores, axis=1) + 1  # классы 1..M

    # Отрисовка регионов
    plt.figure(figsize=(10, 8))
    if M == 4:
        colors_regions = np.array([[255, 125, 125],
                                   [255, 199, 102],
                                   [82, 148, 255],
                                   [160, 232, 160]])/255
        colors_classes = np.array([[255, 0, 0],
                                   [255, 165, 0],
                                   [0, 0, 255],
                                   [0, 255, 0]])/255

    for cls_id in range(1, M + 1):
        mask = (maxScore == cls_id)
        plt.scatter(
            xGrid[mask, 0], xGrid[mask, 1],
            s=5, alpha=0.3, c=colors_regions[cls_id - 1],
            label=f'class {cls_id} region'
        )

    # Обучающие выборки
    for cls_id in range(1, M + 1):
        mask = (Y == cls_id)
        plt.scatter(
            X[mask, 0], X[mask, 1],
            s=30, edgecolors='k', linewidth=0.8,
            label=f'class {cls_id}',
            color=colors_classes[cls_id-1]
        )

    # Опорные векторы
    sv_all = np.vstack(sv_all)
    plt.scatter(
        sv_all[:, 0], sv_all[:, 1],
        s=100, facecolors='none', edgecolors='k', linewidth=2,
        label='Support Vector'
    )

    plt.legend()
    plt.axis('tight')
    plt.title('One-vs-Rest SVM Decision Regions')
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
#         mu2 = (1 / 8) * dmij.T @ inv((C[:, :, i] / 2 + C[:, :, j] / 2)) @ dmij \
#               + 0.5 * np.log((dti + dtj) / (2 * np.sqrt(dti * dtj)))
#
#         PIJB[i, j] = np.sqrt(pw[j] / pw[i]) * np.exp(-mu2)
#         PIJB[j, i] = np.sqrt(pw[i] / pw[j]) * np.exp(-mu2)
#
# for i in range(M):
#     PIJB[i, i] = 1 - PIJB[i, :].sum()
#     PIJ[i, i] = 1 - PIJ[i, :].sum()

# ========== 4. Тестирование методом статистических испытаний ==========

Pcv = np.zeros((M, M))  # SVM one-vs-rest
# Pc_ = np.zeros((M, M))  # теоретический ГСВ

for k_iter in range(K):
    for i in range(M):
        x, _ = randncor(n, 1, C[:, :, i])
        x = x + m_means[:, i].reshape(-1, 1)
        x_vec = x.flatten()  # (n,)

        # ГСВ классификатор
        # u = np.zeros(M)
        # for j in range(M):
        #     diff = x_vec - m_means[:, j]
        #     u[j] = -0.5 * diff.T @ C_[:, :, j] @ diff \
        #            - 0.5 * np.log(det(C[:, :, j])) + np.log(pw[j])
        # iai_gsv = np.argmax(u)
        # Pc_[i, iai_gsv] += 1

        # SVM one-vs-rest: собираем scores от всех классификаторов
        Scores = []
        for j in range(M):
            clf = svm_strs[j]
            score = clf.predict_proba(x_vec.reshape(1, -1))[0, 1]  # вероятность j-го класса
            Scores.append(score)

        iai_svm = np.argmax(Scores)  # индекс класса с max score
        Pcv[i, iai_svm] += 1

# Pc_ = Pc_ / K
Pcv = Pcv / K

# print('Теоретическая матрица вероятностей ошибок')
# print(np.round(PIJ, 4))
# print('\nЭкспериментальная матрица вероятностей ошибок (ГСВ)')
# print(np.round(Pc_, 4))
print('\nЭкспериментальная матрица вероятностей ошибок SVM (One-vs-Rest)')
print(np.round(Pcv, 4))
