def prefix_z(S):
    n = len(S)
    l, r = 0, 0
    zp = [0 for i in range(n)]
    for i in range(1, n):
        if i >= r:
            zp[i] = str_comp(S, n, 0, i)
            l = i
            r = l + zp[i]
        else:
            j = i - l
            if zp[j] < r - i:
                zp[i] = zp[j]
            else:
                zp[i] = r - i + str_comp(S, n, r - i, r)
                l = i
                r = l + zp[i]


def str_comp(S, n, i1, i2):
    return 0