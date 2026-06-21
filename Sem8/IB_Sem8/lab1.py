N = 8  # количество раундов
F16 = 0xFFFF  # маска 16 бит
razmerK = 64  # размер ключа в битах

msg = 0x123436789ABCDEF0  # 64-битное исходное сообщение
K = 0x57EB705CFA9CF672  # 64-битный ключ, 4 x 16


def vpravo16(x, t):
    return ((x >> t) | (x << (16 - t))) & F16


def vlevo16(x, t):
    return ((x << t) | (x >> (16 - t))) & F16


def vpravo64(x, t):
    t = t % 64
    return (x >> t) | (x << (64 - t))


def Ki(i):
    shifted = vpravo64(K, i * 7)
    mixed = shifted ^ vpravo64(K, i * 23)
    return mixed & F16


def F(a, k):
    ak = (a + k) & F16
    f1 = ak ^ vlevo16(ak, 7)
    return f1


def shifr(blok):

    a = (blok >> 48) & F16
    b = (blok >> 32) & F16
    c = (blok >> 16) & F16
    d = blok & F16

    for i in range(N):
        rk = Ki(i)

        oldA, oldB, oldC, oldD = a, b, c, d

        print(f"in  {i} A={oldA:04X} B={oldB:04X} C={oldC:04X} D={oldD:04X}")

        newA = F(oldA, rk)
        newB = newA ^ oldB
        newC = newA ^ oldC
        newD = newA ^ oldD

        if i == N - 1:
            a, b, c, d = oldA, newB, newC, newD
        else:
            a, b, c, d = newB, newC, newD, oldA

        print(f"out {i} A={a:04X} B={b:04X} C={c:04X} D={d:04X}")

    shifroblok = (a << 48) | (b << 32) | (c << 16) | d
    return shifroblok


def rasshifr(blok):

    a = (blok >> 48) & F16
    b = (blok >> 32) & F16
    c = (blok >> 16) & F16
    d = blok & F16

    for i in range(N - 1, -1, -1):
        rk = Ki(i)

        oldA, oldB, oldC, oldD = a, b, c, d

        print(f"in  {i} A={oldA:04X} B={oldB:04X} C={oldC:04X} D={oldD:04X}")

        newA = F(oldA, rk)
        newB = newA ^ oldB
        newC = newA ^ oldC
        newD = newA ^ oldD

        if i == 0:
            a, b, c, d = oldA, newB, newC, newD
        else:
            a, b, c, d = newD, oldA, newB, newC

        print(f"out {i} A={a:04X} B={b:04X} C={c:04X} D={d:04X}")

    shifroblok = (a << 48) | (b << 32) | (c << 16) | d
    return shifroblok


if __name__ == "__main__":
    print(f"{msg:016X}")

    cmsg = shifr(msg)
    print(f"{cmsg:016X}")

    dmsg = rasshifr(cmsg)
    print(f"{dmsg:016X}")
