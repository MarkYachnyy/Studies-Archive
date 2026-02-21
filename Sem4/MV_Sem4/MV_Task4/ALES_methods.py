import math
import time

import matplotlib.pyplot as plt
import numpy as np


def LU(A, B):
    n = len(A)
    L = [[0] * n for _ in range(n)]
    U = [[0] * n for _ in range(n)]
    for i in range(n):
        U[i][i] = 1

    L[0][0] = A[0][0]
    for m in range(0, n):
        j = m
        for i in range(j, n):
            L[i][j] = A[i][j]
            for k in range(0, j):
                L[i][j] -= L[i][k] * U[k][j]

        i = m
        for j in range(1, n):
            U[i][j] = A[i][j]
            for k in range(0, i):
                U[i][j] -= L[i][k] * U[k][j]
            U[i][j] /= L[i][i]

    x = [0] * n
    y = [0] * n
    for s in range(0, n):
        y[s] = B[s]
        for k in range(s):
            y[s] -= L[s][k] * y[k]
        y[s] /= L[s][s]

    for s in reversed(range(0, n)):
        x[s] = y[s]
        for k in range(0, n - s - 1):
            x[s] -= U[s][s + k + 1] * x[s + k + 1]
        x[s] /= U[s][s]

    return x


def jacobi(A, B, e):
    N = 1
    begin_time = time.perf_counter_ns()
    n = len(A)
    x = [1] * n
    x_next = [B[i] for i in range(n)]
    for i in range(n):
        for j in range(n):
            if i == j:
                continue
            x_next[i] -= A[i][j] * x[j]
        x_next[i] /= A[i][i]
    while math.sqrt(sum([(x_next[i] - x[i]) ** 2 for i in range(len(x))])) > e:
        N += 1
        x = x_next.copy()
        x_next = [B[i] for i in range(n)]
        for i in range(n):
            for j in range(n):
                if i == j: continue
                x_next[i] -= A[i][j] * x[j]
            x_next[i] /= A[i][i]
    work_time = time.perf_counter_ns() - begin_time
    return x, N, work_time / 1e6


def gauss_zeudel(A, B, e):
    N = 1
    begin_time = time.perf_counter_ns()
    n = len(A)
    x = [1] * n
    x_next = [B[i] for i in range(n)]
    for i in range(n):
        for j in range(i):
            x_next[i] -= A[i][j] * x_next[j]
        for j in range(i + 1, n):
            x_next[i] -= A[i][j] * x[j]
        x_next[i] /= A[i][i]
    while math.sqrt(sum([(x_next[i] - x[i]) ** 2 for i in range(len(x))])) > e:
        N += 1
        x = x_next.copy()
        x_next = [B[i] for i in range(n)]
        for i in range(n):
            for j in range(i):
                x_next[i] -= A[i][j] * x_next[j]
            for j in range(i + 1, n):
                x_next[i] -= A[i][j] * x[j]
            x_next[i] /= A[i][i]
    work_time = time.perf_counter_ns() - begin_time
    return x, N, work_time / 1e6


def gz_matrix(A, B, e):
    N = 1

    A = np.array(A)
    B = np.array(B)
    U = np.triu(A,1)
    L = np.tril(A, -1)
    D = np.diag(np.diag(A))

    begin_time = time.perf_counter_ns()
    n = len(A)
    x = np.array([1] * n)

    m1 = np.linalg.inv(D + L)
    m2 = np.dot(m1, B)
    m3 = np.dot(m1, U)
    x_next = m2 - np.dot(m3, x)

    while math.sqrt(sum([(x_next[i] - x[i]) ** 2 for i in range(len(x))])) > e:
        N += 1
        x = x_next.copy()
        x_next = m2 - np.dot(m3, x)
    work_time = time.perf_counter_ns() - begin_time
    return x, N, work_time / 1e6


A = [[8, -1, -1, 2], [1, 6, -2, -2], [2, 1, -5, 1], [1.25, -1.25, 1.25, -5]]
B = [11, -7, 2, -2.5]
e_vals = [i * 1e-6 for i in range(1, 10000)]
print(gauss_zeudel(A, B, 0.001))
