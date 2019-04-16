import sys

text_file = open(sys.argv[1], "r")

lines = text_file.read().split('\n')

total = 0
for each in lines:
    if each:
        total += int(each)

total = total / 2462
total_in_ms = total / 1000000
print("Average time ns for", sys.argv[1], ": ", total)
print("Average time ms for", sys.argv[1], ": ", total_in_ms)
