import re


class Flat:
    def __init__(self, district: str, room_count: int, area: int, kitchen_area: int, price: int):
        if room_count <= 0 or area <= 0 or kitchen_area <= 0 or price <= 0:
            raise ValueError("Invalid input parameters for flat")
        self.district = district
        self.room_count = room_count
        self.area = area
        self.kitchen_area = kitchen_area
        self.price = price

    def __str__(self):
        return (f"{str(self.district)}" +
                f" {str(self.room_count)}" +
                f" {str(self.area)}" +
                f" {str(self.kitchen_area)}" +
                f" {str(self.price)}")

    @staticmethod
    def parse_flat(line: str):
        args = line.split()
        if len(args) != 5:
            raise ValueError("Failed to parse flat")
        res = Flat(args[0], *map(int, args[1:]))
        return res


def get_filters(filename, size=4):
    lines = []
    file = open(filename)
    for i in range(size):
        lines.append(file.readline())
    file.close()

    return [parse_filter(line) for line in lines]


def parse_filter(line):
    values = [s.strip() for s in re.split("\\s*:\\s*", line)]
    if len(values) > 2 or len(values) < 1:
        raise ValueError("Incorrect filter line format")
    if len(values) == 1:
        return lambda x: True
    else:
        floor = lambda x: True
        ceiling = lambda x: True

        if values[0] != '':
            a = int(values[0])
            floor = lambda x: x >= a

        if values[1] != '':
            b = int(values[1])
            ceiling = lambda x: x <= b

        return lambda x: (floor(x) and ceiling(x))


def check_flat(flat, filters):
    if not (filters[0](flat.room_count) and filters[0](flat.room_count)):
        return False
    if not (filters[1](flat.area) and filters[1](flat.area)):
        return False
    if not (filters[2](flat.kitchen_area) and filters[2](flat.kitchen_area)):
        return False
    if not (filters[3](flat.price) and filters[3](flat.price)):
        return False
    return True


try:
    input_flats = open("input_flats.txt", encoding="UTF-8")
    flats = [Flat.parse_flat(line) for line in input_flats]
    input_flats.close()

    filters = get_filters("input_filter05.txt")

    res = []
    for flat in flats:
        if check_flat(flat, filters):
            res.append(flat)

    output_file = open('output.txt', 'w', encoding='UTF-8')
    for flat in res:
        output_file.write(str(flat) + '\n')

except ValueError as ve:
    print(f"Error: {str(ve)}")
except FileNotFoundError as fe:
    print("Error: specified input file does not exist")
