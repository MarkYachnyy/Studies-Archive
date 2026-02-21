import random as rnd

res = []
for i in range(50):
    x1 = int(rnd.uniform(0, 100))
    x2 = int(rnd.uniform(0, 100))
    y = 1
    res.append((x1, x2, y))


for i in range(50):
    x1 = int(rnd.uniform(150, 250))
    x2 = int(rnd.uniform(150, 250))
    y = -1
    res.append((x1, x2, y))

for i in range(50):
    x1 = int(rnd.uniform(50, 150))
    x2 = int(rnd.uniform(150, 250))
    y = -1
    res.append((x1, x2, y))

for i in range(50):
    x1 = int(rnd.uniform(150, 250))
    x2 = int(rnd.uniform(50, 150))
    y = -1
    res.append((x1, x2, y))

file = open("perfect.txt", 'w')
for c in res:
    file.write(f"{c[0]} {c[1]} {c[2]}\n")
