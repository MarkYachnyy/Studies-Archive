import math
import string
from typing import Iterator


def xor_bytes(a: bytes, b: bytes) -> bytes:
    return bytes(x ^ y for x, y in zip(a, b))


def b64decode(s: str) -> bytes:
    import base64
    return base64.b64decode(s)


def iter_keystream_bytes(seed: int) -> Iterator[int]:
    n=6
    R = ((seed % 1_000_000) + 1) / 1_000_001
    z = 0.011

    while True:
        Ti = int(R * 2 ** 32)

        b0 = (Ti >> 0) & 0xFF
        b1 = (Ti >> 8) & 0xFF
        b2 = (Ti >> 16) & 0xFF
        b3 = (Ti >> 24) & 0xFF

        yield b0
        yield b1
        yield b2
        yield b3

        z += 10 ** (-n)
        R = math.fmod(R / z + math.pi, 1.0)


def decrypt(ciphertext: bytes, seed: int) -> bytes:
    keystream = iter_keystream_bytes(seed)
    ks = bytes(next(keystream) for _ in range(len(ciphertext)))
    return xor_bytes(ciphertext, ks)


def looks_plausible_plaintext(b: bytes) -> bool:
    if not b.startswith(b"IB{"):
        return False
    try:
        s = b.decode("utf-8")
    except UnicodeDecodeError:
        return False

    printable = set(string.printable) | set("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя")
    good = sum(1 for ch in s if ch in printable or ch in "\n\r\t")
    return good / max(1, len(s)) > 0.85


t_from = 1700160113
t_to = 1700161913
ciphertext = b64decode("vXFDWdgKRJhPUwK99N/XijTchVrXBxrIKDcn9+ThbnbFePf7OJv9WzUE2l6kCdp7eVcWy6Yo0+e8duJApD1Zv0UASm4jGNxjdTzYNgFnuTcXXMBFmy80ogUuNHK9I9pMZNTUA41H5E6F6BKd")

for seed in range(t_from, t_to + 1):
    plain = decrypt(ciphertext=ciphertext, seed=seed)
    if looks_plausible_plaintext(plain):
        print(seed)
        print(plain.decode("utf-8"))






