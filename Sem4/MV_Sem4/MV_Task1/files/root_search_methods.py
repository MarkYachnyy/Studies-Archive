def bissection(func, a, b, e):
    left = a
    right = b
    mid = (a + b) / 2
    left_val = func(x=left)
    right_val = func(x=right)
    while right - left > 2 * e:
        mid_val = func(x=mid)
        if sign(left_val) == sign(mid_val):
            left = mid
            left_val = mid_val
        elif sign(right_val) == sign(mid_val):
            right = mid
            right_val = mid_val
        mid = (left + right) / 2
    return mid


def chord(func, a, b, e):
    left = a
    right = b
    left_val = func(x=left)
    right_val = func(x=right)
    delta = e + 1
    mid = b
    while delta > e:
        new_mid = left - left_val * (right - left) / (right_val - left_val)
        delta = abs(new_mid - mid)
        mid = new_mid
        mid_val = func(x=mid)
        if sign(left_val) == sign(mid_val):
            left = mid
            left_val = mid_val
        elif sign(right_val) == sign(mid_val):
            right = mid
            right_val = mid_val
    return mid


def sign(a):
    if a > 0:
        return 1
    elif a == 0:
        return 0
    else:
        return -1
