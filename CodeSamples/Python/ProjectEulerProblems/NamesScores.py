# Project Euler problem 22

# Open File for reading
file = open("NamesList.txt", "r")

# split contents to create a list of names
namesList = file.read().split('"')[1::2]

# Sort list alphabetically
namesList.sort()


# Dictionary function
def dictionary(x):
    return{
        'a': 1,
        'b': 2,
        'c': 3,
        'd': 4,
        'e': 5,
        'f': 6,
        'g': 7,
        'h': 8,
        'i': 9,
        'j': 10,
        'k': 11,
        'l': 12,
        'm': 13,
        'n': 14,
        'o': 15,
        'p': 16,
        'q': 17,
        'r': 18,
        's': 19,
        't': 20,
        'u': 21,
        'v': 22,
        'w': 23,
        'x': 24,
        'y': 25,
        'z': 26,
    }[x]


totalScore = 0

for index, name in enumerate(namesList):
    temp = 0
    for character in name:
        temp += dictionary(character.lower())
    score = temp * (index + 1)
    totalScore += score

print(totalScore)
