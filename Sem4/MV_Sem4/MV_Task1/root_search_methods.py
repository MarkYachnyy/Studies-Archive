def bissection(func, a, b, e):
    i = 0
    left = a
    right = b
    mid = (a + b) / 2
    left_val = func(left)
    right_val = func(right)
    while right - left > 2 * e:
        i += 1
        mid_val = func(mid)
        if sign(left_val) == sign(mid_val):
            left = mid
            left_val = mid_val
        elif sign(right_val) == sign(mid_val):
            right = mid
            right_val = mid_val
        mid = (left + right) / 2
    return (mid, i)


def chord(func, a, b, e):
    left = a
    right = b
    left_val = func(left)
    right_val = func(right)
    delta = e + 100
    mid = b
    while delta > e:
        new_mid = left - left_val * (right - left) / (right_val - left_val)
        delta = abs(new_mid - mid)
        mid = new_mid
        mid_val = func(mid)
        if sign(left_val) == sign(mid_val):
            left = mid
            left_val = mid_val
        elif sign(right_val) == sign(mid_val):
            right = mid
            right_val = mid_val
    return mid


def newton(func, diff, a, b, e):
    i = 0
    left = a
    right = b
    left_val = func(left)
    right_val = func(right)
    delta = e + 100
    mid = (left + right) / 2
    while delta > e:
        i += 1
        new_mid = mid - func(mid) / diff(mid)
        delta = abs(new_mid - mid)
        mid = new_mid
        mid_val = func(mid)
        if sign(left_val) == sign(mid_val):
            left_val = mid_val
        elif sign(right_val) == sign(mid_val):
            right_val = mid_val
    return (mid, i)


def sign(a):
    if a > 0:
        return 1
    elif a == 0:
        return 0
    else:
        return -1
