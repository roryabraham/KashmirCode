# project Euler problem 9

import math


def pythagoreanTriplet(x, y, z):
    if not x < y < z:
        return False
    else:
        a = x * x
        b = y * y
        c = z * z
        if a + b == c:
            return True
        else:
            return False


for x in range(1, 1000):
    for y in range(x+1, 1000-x):
        z = math.sqrt((x * x) + (y * y))
        if x + y + z == 1000 and pythagoreanTriplet(x, y, z):
            print(x * y * z)


exit(0)
