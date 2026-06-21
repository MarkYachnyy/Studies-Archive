import sys
from typing import List


def main():
    # Заданные параметры
    e = 12331
    n = 517817326500043

    # Вычисленные простые числа
    p = 2432371
    q = 212885833

    # Вычисляем функцию Эйлера
    phi = (p - 1) * (q - 1)
    print(f"phi = {phi}")

    g, x, y = extended_gcd(e, phi)

    # Обратный элемент для e по модулю phi (вычислен заранее)
    d = x % phi
    print(f"d = {d}")

    # Зашифрованное сообщение
    c_text = "14093692992765,11009334920011,48625371117033,84788101545613,74471402080505,24745114604071,975".split(',')
    # Расшифровываем каждую часть
    numbers = []
    for c_str in c_text:
        c_msg = int(c_str)  # преобразуем в целое число
        msg = pow(c_msg, d, n)  # дешифруем (возведение в степень по модулю)
        msg2 = pow(msg, e, n)
        print(msg2)
        numbers.append(msg)

    # Объединяем числа в одну строку
    str_numbers = ''.join(str(num) for num in numbers)

    # Преобразуем пары чисел в символы и выводим результат
    #print(parse_str(str_numbers))


def parse_str(str_numbers: str) -> str:
    """
    Преобразует строку из пар чисел в ASCII строку.

    Например: "726978" -> "Hi" (ASCII 72='H', 69='i')
    """
    bytes_list = []
    for i in range(0, len(str_numbers), 2):
        num_str = str_numbers[i:i + 2]
        bytes_list.append(int(num_str))

    # Преобразуем байты в строку
    return ''.join(chr(b) for b in bytes_list)


def extended_gcd(a, b):
    if b == 0:
        return (a, 1, 0)  # НОД, коэффициент при a, коэффициент при b
    else:
        g, x1, y1 = extended_gcd(b, a % b)
        x = y1
        y = x1 - (a // b) * y1
        return (g, x, y)


if __name__ == "__main__":
    main()