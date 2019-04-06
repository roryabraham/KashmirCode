# Project Euler Problem 36
# The decimal number, 585 = 1001001001, is palindromic in both bases
# Find the sum of all numbers, less than one million, which are palindromic in base 10 and base 2

sum = 0


def is_palindrome(string):
    reverse_string = ''.join(list(reversed(string)))
    # print(reverse_string)
    if reverse_string == string:
        return True
    else:
        return False


# is_palindrome(number)


for num in range(0, 1000000):
    base_ten_string = str(num)
    binary_string = str(format(num, 'b'))
    if is_palindrome(base_ten_string) and is_palindrome(binary_string):
        sum += num

print(sum)


