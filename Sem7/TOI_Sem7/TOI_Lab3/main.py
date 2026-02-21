import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import multivariate_normal, norm
from scipy.linalg import cholesky, det, inv


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


def main():
    # 1. Задание исходных данных
    n = 2
    M = 2  # размерность признакового пространства и число классов
    K = 1000  # количество статистических испытаний

    # Априорные вероятности, математические ожидания и матрицы ковариации классов
    pw = np.array([0.4, 0.6])
    pw = pw / np.sum(pw)

    m = np.array([[2, -3], [1, 10]]).T  # математические ожидания

    # Матрицы ковариации
    C = np.zeros((n, n, M))
    C[:, :, 0] = np.array([[4, -2], [-2, 4]])
    C[:, :, 1] = np.array([[5, 1], [1, 5]])

    # Обратные матрицы ковариации
    C_ = np.zeros_like(C)
    for k in range(M):
        C_[:, :, k] = inv(C[:, :, k])

    # 2. Расчет матриц вероятностей ошибок распознавания
    PIJ = np.zeros((M, M))
    PIJB = np.zeros((M, M))
    l0_ = np.zeros((M, M))

    for i in range(M):
        for j in range(i + 1, M):
            dmij = m[:, i] - m[:, j]
            l0_[i, j] = np.log(pw[j] / pw[i])

            dti = det(C[:, :, i])
            dtj = det(C[:, :, j])

            # Вычисление следов
            trij = np.trace(C_[:, :, j] @ C[:, :, i] - np.eye(n))
            trji = np.trace(np.eye(n) - C_[:, :, i] @ C[:, :, j])

            trij_2 = np.trace((C_[:, :, j] @ C[:, :, i] - np.eye(n)) @
                              (C_[:, :, j] @ C[:, :, i] - np.eye(n)))
            trji_2 = np.trace((np.eye(n) - C_[:, :, i] @ C[:, :, j]) @
                              (np.eye(n) - C_[:, :, i] @ C[:, :, j]))

            # Параметры нормального распределения
            mg1 = 0.5 * (trij + dmij.T @ C_[:, :, i] @ dmij - np.log(dti / dtj))
            Dg1 = 0.5 * trij_2 + dmij.T @ C_[:, :, j] @ C[:, :, i] @ C_[:, :, j] @ dmij

            mg2 = 0.5 * (trji - dmij.T @ C_[:, :, j] @ dmij + np.log(dtj / dti))
            Dg2 = 0.5 * trji_2 + dmij.T @ C_[:, :, i] @ C[:, :, j] @ C_[:, :, i] @ dmij

            sD1 = np.sqrt(Dg1)
            sD2 = np.sqrt(Dg2)

            # Вероятности ошибок
            PIJ[i, j] = norm.cdf(l0_[i, j], mg1, sD1)
            PIJ[j, i] = 1 - norm.cdf(l0_[i, j], mg2, sD2)

            # Расстояние Бхатачария и границы Чернова
            C_avg = (C[:, :, i] / 2 + C[:, :, j] / 2)
            mu2 = (1 / 8) * dmij.T @ inv(C_avg) @ dmij + 0.5 * np.log((dti + dtj) / (2 * np.sqrt(dti * dtj)))

            PIJB[i, j] = np.sqrt(pw[j] / pw[i]) * np.exp(-mu2)
            PIJB[j, i] = np.sqrt(pw[i] / pw[j]) * np.exp(-mu2)

        # Диагональные элементы
        PIJB[i, i] = 1 - np.sum(PIJB[i, :])
        PIJ[i, i] = 1 - np.sum(PIJ[i, :])

    # Задание варианта: Сравнение ошибки второго в теоретической матрице и матрице Чернова
    # Пустые массивы для значений расстояний и ошибок
    ss_ch = np.arange(0.5, 20.5, 0.5)
    ss_th = np.zeros(len(ss_ch))
    errs_th = np.zeros(len(ss_ch))
    errs_ch = np.zeros(len(ss_ch))
    # Цикл по расстояниям Бхаттачария
    for k in range(len(ss_ch)):

        # Скопированный из предыдущий части код для вычисления теоретический ошибки
        PIJ_th = np.zeros((M, M))
        m1 = np.array([[2, -3], [1, 0.3 + 0.6 * k]]).T # Изменение мат.ожиданий для изменения расстояний
        dmij = m1[:, 0] - m1[:, 1]
        l0_[0, j] = np.log(pw[j] / pw[0])

        dti = det(C[:, :, 0])
        dtj = det(C[:, :, 1])

        # Вычисление следов
        trij = np.trace(C_[:, :, 1] @ C[:, :, 0] - np.eye(n))
        trji = np.trace(np.eye(n) - C_[:, :, 0] @ C[:, :, 1])

        trij_2 = np.trace((C_[:, :, 1] @ C[:, :, 0] - np.eye(n)) @
                          (C_[:, :, 1] @ C[:, :, 0] - np.eye(n)))
        trji_2 = np.trace((np.eye(n) - C_[:, :, 0] @ C[:, :, 1]) @
                          (np.eye(n) - C_[:, :, 0] @ C[:, :, 1]))

        # Параметры нормального распределения
        mg1 = 0.5 * (trij + dmij.T @ C_[:, :, 0] @ dmij - np.log(dti / dtj))
        Dg1 = 0.5 * trij_2 + dmij.T @ C_[:, :, 1] @ C[:, :, 0] @ C_[:, :, 1] @ dmij

        mg2 = 0.5 * (trji - dmij.T @ C_[:, :, 1] @ dmij + np.log(dtj / dti))
        Dg2 = 0.5 * trji_2 + dmij.T @ C_[:, :, 0] @ C[:, :, 1] @ C_[:, :, 0] @ dmij

        sD1 = np.sqrt(Dg1)
        sD2 = np.sqrt(Dg2)

        # Вероятности ошибок
        PIJ_th[0, 1] = norm.cdf(l0_[0, 1], mg1, sD1)
        PIJ_th[1, 0] = 1 - norm.cdf(l0_[0, 1], mg2, sD2)

        # Расстояние Бхатачария и границы Чернова
        C_avg = (C[:, :, 0] / 2 + C[:, :, 1] / 2)
        ss_th[k] = (1 / 8) * dmij.T @ inv(C_avg) @ dmij + 0.5 * np.log((dti + dtj) / (2 * np.sqrt(dti * dtj)))

        mu2_ch = ss_ch[k]
        errs_ch[k] = np.sqrt(pw[1]/pw[j]) * np.exp(-mu2_ch)
        errs_th[k] = PIJ_th[0, 1]

    # Вывод результатов сравнения
    plt.figure()
    plt.title("Зависимость ошибки 2 рода от расстояния")
    plt.plot(ss_ch, errs_ch, label="Ошибка в матрице Чернова")
    plt.plot(ss_th, errs_th, label="Ошибка в теоретической матрице")
    plt.legend()
    plt.show()

    # 3. Тестирование алгоритма методом статистических испытаний
    Pc_ = np.zeros((M, M))  # экспериментальная матрица вероятностей ошибок

    for k in range(K):  # цикл по числу испытаний
        for i in range(M):  # цикл по классам
            # генерация образа i-го класса
            x, _ = randncor(n, 1, C[:, :, i])
            x = x + m[:, i].reshape(-1, 1)

            # вычисление значения разделяющих функций
            u = np.zeros(M)
            for j in range(M):
                x_diff = x.flatten() - m[:, j]
                u[j] = (-0.5 * x_diff.T @ C_[:, :, j] @ x_diff -
                        0.5 * np.log(det(C[:, :, j])) + np.log(pw[j]))

            # определение максимума
            iai = np.argmax(u)
            Pc_[i, iai] += 1  # фиксация результата распознавания

    Pc_ = Pc_ / K

    print('Теоретическая матрица вероятностей ошибок:')
    print(PIJ)
    print('\nМатрица вероятностей ошибок на основе границы Чернова:')
    print(PIJB)
    print('\nЭкспериментальная матрица вероятностей ошибок:')
    print(Pc_)

    # 4. Визуализация областей принятия решений для двумерного случая
    if n == 2:
        Es1 = pw[0] * PIJ[0, 1] + pw[1] * PIJ[1, 0]
        Es2 = np.sqrt(pw[0] * pw[1]) * np.exp(-mu2)  # граница Чернова для суммарной ошибки
        Es3 = pw[0] * Pc_[0, 1] + pw[1] * Pc_[1, 0]

        print('\nОценки суммарных ошибок:')
        print(f"Теоретическая: {Es1:.4f}, Чернова: {Es2:.4f}, Экспериментальная: {Es3:.4f}")

        # Определение границ графика
        D = 3 * np.eye(2)
        xmin1 = -3 * np.sqrt(np.max(D[0, :])) + np.min(m[0, :])
        xmax1 = 3 * np.sqrt(np.max(D[0, :])) + np.max(m[0, :])
        xmin2 = -3 * np.sqrt(np.max(D[1, :])) + np.min(m[1, :])
        xmax2 = 3 * np.sqrt(np.max(D[1, :])) + np.max(m[1, :])

        x1 = np.arange(xmin1, xmax1, 0.1)
        x2 = np.arange(xmin2, xmax2, 0.1)

        X1, X2 = np.meshgrid(x1, x2)
        x12 = np.column_stack([X1.ravel(), X2.ravel()])

        plt.figure(figsize=(10, 8))

        for i in range(M):
            # Контуры плотностей распределения
            f2 = multivariate_normal(m[:, i], C[:, :, i]).pdf(x12)
            f3 = f2.reshape(X1.shape)

            contour = plt.contour(X1, X2, f3, levels=[0.01, 0.5 * np.max(f3)],
                                  colors='b', linewidths=0.75)
            plt.clabel(contour, inline=True, fontsize=8)

            # Разделяющие границы
            for j in range(i + 1, M):
                wij = C_[:, :, i] @ m[:, i] - C_[:, :, j] @ m[:, j]
                wij0 = -0.5 * (m[:, i].T @ C_[:, :, i] @ m[:, i] -
                               m[:, j].T @ C_[:, :, j] @ m[:, j])

                f4 = (wij @ x12.T + wij0 -
                      0.5 * np.log(det(C[:, :, i]) / det(C[:, :, j])))

                fd = -0.5 * (C_[:, :, i] - C_[:, :, j]) @ x12.T
                f4 = f4 + np.sum(x12.T * fd, axis=0)

                f5 = f4.reshape(X1.shape)

                contour_ = plt.contour(X1, X2, f5, levels=[l0_[i, j]],
                                       colors='k', linewidths=1.25)

        plt.xlim([xmin1, xmax1])
        plt.ylim([xmin2, xmax2])
        plt.grid(True)
        plt.title('Области локализации классов и разделяющие границы', fontsize=14)
        plt.xlabel('x1', fontsize=12)
        plt.ylabel('x2', fontsize=12)

        # Добавление информации о вероятностях
        text_str = f'pw = {pw}'
        plt.text(0.05, 0.95, text_str, transform=plt.gca().transAxes,
                 verticalalignment='top', bbox=dict(boxstyle='round', facecolor='lightgray', alpha=0.8))

        plt.tight_layout()
        plt.show()

if __name__ == "__main__":
    main()