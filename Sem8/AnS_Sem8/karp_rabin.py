def KR(P, T, q):
    n = len(T)
    m = len(P)
    p2m = 1
    for i in range(m - 1):
        p2m = (p2m * 2) % q
    hp = gooner(P, m, q)
    ht = gooner(T, m, q)

    for j in range(n - m + 1):
        if ht == hp:
            k = 0
            while k < m and P[k] == T[j + k]:
                k += 1
            if k == m:
                print("Вхождение")

        ht = ((ht - p2m * T[j]) * 2 + T[j + m]) % q


def gooner(P, m, q):
    res = 0
    for i in range(m):
        res = (res * 2 + P[i]) % q
    return res
