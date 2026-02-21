def split_into_sentences(text):
    res = []
    in_sentence = False
    sentence = ''
    dividers = '?!.'
    for c in text:
        if in_sentence:
            sentence += c
            if c in dividers:
                res.append(sentence)
                sentence = ''
                in_sentence = False
        else:
            if c not in dividers + ' \n':
                sentence += c
                in_sentence = True
    return res


def longest_sentence(text):
    res = ''
    max_words = 0
    sentences = split_into_sentences(text)
    for sentence in sentences:
        wc = word_count(sentence)
        if wc > max_words:
            max_words = wc
            res = sentence
    return res, max_words


def word_count(sentence):
    res = []
    word = ''
    for c in sentence:
        if c.lower() in 'абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyz0123456789':
            word += c
        elif word != '':
            res.append(word)
            word = ''
    return len(res)


def task_4():
    try:
        input_file = open("input_01.txt", encoding='UTF-8')
        text = ''
        for line in input_file:
            text += line
        input_file.close()
        output_file = open("output.txt", 'w', encoding='UTF-8')
        output_text = longest_sentence(text)
        output_file.write(output_text[0] + ' ' + str(output_text[1]))
    except FileNotFoundError:
        print('Error: Specified input file does not exist')


task_4()
