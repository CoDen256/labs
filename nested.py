import sys
string = """

program(list(stmt(assign_stmt(var(a), expr(term(factor(int('10')))))), 
list(stmt(while_stmt(expr(term(factor(var(a)))), 
list(stmt(print(expr(term(factor(var(a)))))), 
list(stmt(if_stmt(expr(term(factor(var(a))), operator('MINUS'), expr(term(factor(int('5'))))), list(stmt(print(expr(term(factor(expr(term(factor(int('1')))))))))))), list(stmt(assign_stmt(var(a), expr(term(factor(var(a))), operator('MINUS'), 
expr(term(factor(int('1')))))))))))))))
"""


level = 0
result = ""
for chr in sys.argv[1]:
    if chr == "(":
        print(chr, end="")
        print("\n", end="")
        print(" "*level, end="")
        level += 1
    elif chr == ")":
        print("\n", end="")
        print(" "*level, end="")
        level -= 1
        print(chr, end="")
    elif chr == ",":
        print(chr, end="")
        print("\n", end="")
        print(" "*level, end="")
    elif chr = " ":
        continue
    else:
        print(chr, end="")

    
