RUSSIAN_FREQ = {
    'о': 10.97, 'е': 8.45, 'а': 8.01, 'и': 7.35, 'н': 6.70,
    'т': 6.26, 'с': 5.47, 'р': 4.73, 'в': 4.54, 'л': 4.40,
    'к': 3.49, 'м': 3.21, 'д': 2.98, 'п': 2.80, 'у': 2.62,
    'я': 2.01, 'ы': 1.90, 'ь': 1.74, 'г': 1.70, 'з': 1.65,
    'б': 1.59, 'ч': 1.44, 'й': 1.21, 'х': 0.97, 'ж': 0.94,
    'ш': 0.73, 'ю': 0.64, 'ц': 0.48, 'щ': 0.36, 'э': 0.32,
    'ф': 0.26, 'ъ': 0.04, 'ё': 0.04
}
#
# RUSSIAN_ALPHABET = 'абвгдеёжзийклмнопрстуфхцчшщъыьэюя'
#
#
# def shift_char(char, shift):
#     """Сдвигает одну русскую букву на заданное число позиций"""
#     if char not in RUSSIAN_ALPHABET:
#         return char
#     idx = RUSSIAN_ALPHABET.index(char)
#     new_idx = (idx + shift) % len(RUSSIAN_ALPHABET)
#     return RUSSIAN_ALPHABET[new_idx]
#
#
# def decrypt_with_shift(text, shift):
#     """Расшифровывает текст с заданным сдвигом"""
#     result = []
#     for ch in text:
#         if ch in RUSSIAN_ALPHABET:
#             result.append(shift_char(ch, -shift))  # минус, чтобы вернуть обратно
#         else:
#             result.append(ch)
#     return ''.join(result)
#
#
# def score_shift(cipher_freq, shift):
#     """
#     Оценивает, насколько хорош данный сдвиг.
#     Для каждой буквы шифротекста сдвигаем её назад на shift, чтобы получить букву
#     открытого текста, и сравниваем её частоту с эталонной.
#     """
#     score = 0.0
#     for cipher_char, observed_freq in cipher_freq.items():
#         if cipher_char in RUSSIAN_ALPHABET:
#             # предполагаемая буква открытого текста
#             idx = RUSSIAN_ALPHABET.index(cipher_char)
#             orig_idx = (idx - shift) % len(RUSSIAN_ALPHABET)
#             orig_char = RUSSIAN_ALPHABET[orig_idx]
#             expected_freq = RUSSIAN_FREQ.get(orig_char, 0)
#             score += observed_freq * expected_freq
#     return score


def read_text_from_file(filename, encoding='utf-8'):
    with open(filename, 'r', encoding=encoding) as file:
        text = file.read()
    return text


def count_russian_symbol_frequencies(text):
    russian_symbols = set('абвгдеёжзийклмнопрстуфхцчшщъыьэюя')

    l = len(text)

    frequencies = []
    for symbol in russian_symbols:
        percentage = text.count(symbol)/l
        frequencies.append((symbol, percentage))

    frequencies.sort(key=lambda x: x[1], reverse=True)

    return frequencies

# new_char_maps = {
#     'а': 'л',
#     'б': 'ш',
#     'в': 'э',
#     'г': 'ъ',
#     'д': 'р',
#     'е': 'г',
#     'ё': 'ё',
#     'ж': 'й',
#     'з': 'о',
#     'и': 'з',
#     'й': 'ф',
#     'к': 'и',
#     'л': 'ь',
#     'м': 'т',
#     'н': 'ж',
#     'о': 'я',
#     'п': 'ю',
#     'р': 'д',
#     'с': 'ц',
#     'т': 'щ',
#     'у': 'к',
#     'ф': 'п',
#     'х': 'ч',
#     'ц': 'в',
#     'ч': 'у',
#     'ш': 'е',
#     'щ': 'б',
#     'ь': 'н',
#     'ы': 'м',
#     'ъ': 'с',
#     'э': 'х',
#     'ю': 'а',
#     'я': 'ы'
# }

new_char_maps = {
    'а': 'л',
    'б': 'ш',
    'в': 'э',
    'г': 'ъ',
    'д': 'р',
    'е': 'г',
    'ё': 'ё',
    'ж': 'й',
    'з': 'о',
    'и': 'з',
    'й': 'ф',
    'к': 'и',
    'л': 'ь',
    'м': 'т',
    'н': 'ж',
    'о': 'я',
    'п': 'ю',
    'р': 'д',
    'с': 'ц',
    'т': 'щ',
    'у': 'к',
    'ф': 'п',
    'х': 'ч',
    'ц': 'в',
    'ч': 'у',
    'ш': 'е',
    'щ': 'б',
    'ь': 'н',
    'ы': 'м',
    'ъ': 'с',
    'э': 'х',
    'ю': 'а',
    'я': 'ы'
}

encrypted_text_orig = read_text_from_file('text.txt')
encrypted_text = encrypted_text_orig.lower()

russian_chars = 'оеаинтсрвлмкдпуяыьгзбчйхжшюцщэфъё'
frequencies = count_russian_symbol_frequencies(encrypted_text)
new_chars = [f[0] for f in frequencies]

print('', *[f"{c[0]} -> {c[1]}\n" for c in frequencies])
# print(russian_chars)
# print(''.join(new_chars))

new_text = ''

auto = False
if auto:
    for i in range(len(encrypted_text)):
        if encrypted_text[i] in russian_chars:
            new_text += russian_chars[new_chars.index(encrypted_text[i])]
        else:
            new_text += encrypted_text[i]
else:
    for i in range(len(encrypted_text)):
        if encrypted_text[i] in russian_chars:
            replacement = new_char_maps[encrypted_text[i]]
            if replacement == '':
                replacement = encrypted_text[i]
            else:
                replacement = replacement.capitalize() if encrypted_text_orig[i].isupper() else replacement
                #replacement = replacement.capitalize()
            new_text += replacement
        else:
            new_text += encrypted_text[i]

print(new_text)