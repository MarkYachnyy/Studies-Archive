def ShiftAnd(P, T):
    m = len(P)
    n = len(T)
    ch_beg = '0'
    ch_end = 'z'
    nA = ord(ch_end) - ord(ch_beg) + 1
    B = [0 for _ in range(nA)]
    for j in range(m):
        B[ord(P[j]) - ord(ch_beg)] |= 1 << (m - 1 - j)
    u_high = 1 << (m - 1)
    M = 0
    for i in range(n):
        M = (M >> 1 | u_high) & B[ord(T[i]) - ord(ch_beg)]
        if M & 1:
            print(f"Вхождение с позиции {i - m + 1}")


def ShiftAndFz(P, T, k):
    m = len(P)
    n = len(T)
    ch_beg = '0'
    ch_end = 'z'
    nA = ord(ch_end) - ord(ch_beg) + 1
    B = [0 for _ in range(nA)]

    for l in range(k + 1):
        break

    for j in range(m):
        B[ord(P[j]) - ord(ch_beg)] |= 1 << (m - 1 - j)
    u_high = 1 << (m - 1)

    M = [0 for _ in range(k + 1)]
    M1 = [0 for _ in range(k + 1)]

    for i in range(n):
        for l in range(k + 1):
            M1[l] = M[l]
            M[l] = (M[l] >> 1 | u_high) & B[ord(T[i]) - ord(ch_beg)]
            if l:
                M[l] |= (M1[l-1] >> 1 | u_high)
            if l == k and (M[l] & 1):
                print("Вхождение")
