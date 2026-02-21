import math


def RungeKutt4(f, x0, y0, xn, n):
    h = (xn - x0) / n
    y = [y0]
    x = [x0]
    xi = x0
    yi = y0
    while xi < xn:
        k0 = f(xi, yi)
        k1 = f(xi + h / 2, yi + h / 2 * k0)
        k2 = f(xi + h / 2, yi + h / 2 * k1)
        k3 = f(xi + h, yi + h * k2)
        yi = yi + h / 6 * (k0 + 2 * k1 + 2 * k2 + k3)
        y.append(yi)
        xi += h
        x.append(xi)
    return x, y


def Adams4(f, x0, y0, xn, n):
    h = (xn - x0) / n
    y = [y0]
    x = [x0]
    xi = x0
    yi = y0
    fl = [f(x0, y0)]
    for i in range(3):
        k0 = f(xi, yi)
        k1 = f(xi + h / 2, yi + h / 2 * k0)
        k2 = f(xi + h / 2, yi + h / 2 * k1)
        k3 = f(xi + h, yi + h * k2)
        yi = yi + h / 6 * (k0 + 2 * k1 + 2 * k2 + k3)
        y.append(yi)
        xi += h
        x.append(xi)
        fl.append(f(xi, yi))

    for i in range(3, n):
        yi = yi + h / 24 * (55 * fl[i] - 59 * fl[i - 1] + 37 * fl[i - 2] - 9 * fl[i - 3])
        xi += h
        y.append(yi)
        x.append(xi)
        fl.append(f(xi, yi))

    return x, y


def func(x, y):
    return y * math.cos(x)


def real_sol(x):
    return math.e ** math.sin(x)


n = 10
x0 = 0
y0 = 1
yn = 10
xR, yR = RungeKutt4(func, x0, y0, yn, n)
_, yA = Adams4(func, x0, y0, yn, n)

print(len(yR), len(yA))
