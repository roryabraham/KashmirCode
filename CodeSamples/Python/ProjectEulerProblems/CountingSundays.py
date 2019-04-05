# Project Euler Problem 19

from enum import Enum
from collections import OrderedDict


class Day(Enum):
    MONDAY = 1
    TUESDAY = 2
    WEDNESDAY = 3
    THURSDAY = 4
    FRIDAY = 5
    SATURDAY = 6
    SUNDAY = 0


month = OrderedDict()
month["January"] = 31
month["February"] = 28
month["March"] = 31
month["April"] = 30
month["May"] = 31
month["June"] = 30
month["July"] = 31
month["August"] = 31
month["September"] = 30
month["October"] = 31
month["November"] = 30
month["December"] = 31

year = 1900
day = Day.MONDAY
count = 0

day_iter = iter(Day)

for year in range(1901, 2000):
    if year % 400 == 0:
        month.update({"February": 29})
    elif year % 100 == 0:
        month.update({"February": 28})
    elif year % 4 == 0:
        month.update({"February": 29})
    else:
        month.update({"February": 28})
    for name, value in month.items():
        for date in range(1, value + 1):
            try:
                day = day_iter.__next__()
            except StopIteration:
                day_iter = iter(Day)
                day = day_iter.__next__()

            if date == 1 and day == Day.SUNDAY:
                count += 1

print(count)
exit(0)
