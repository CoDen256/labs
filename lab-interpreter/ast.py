(INTEGER, PLUS, MINUS, MUL, DIV, LPAREN, RPAREN, ID, ASSIGN,
 BEGIN, END, SEMI, DOT, EOF, PRINT, IF, ELSE, OPEN, CLOSE, FUN, FUN_ID, WHILE) = (
    'INTEGER', 'PLUS', 'MINUS', 'MUL', 'DIV', '(', ')', 'ID', 'ASSIGN',
    'BEGIN', 'END', 'SEMI', 'DOT', 'EOF', 'PRINT', 'IF', "ELSE", "{", "}", "FUN", "FUN_ID", "WHILE"
)

class Token(object):
    def __init__(self, type, value):
        self.type = type
        self.value = value

    def __str__(self):
        return 'Token({type}, {value})'.format(
            type=self.type,
            value=repr(self.value)
        )

    def __repr__(self):
        return self.__str__()

RESERVED_KEYWORDS = {
    'BEGIN': Token('BEGIN', 'BEGIN'),
    'END': Token('END', 'END'),
    'PRINT': Token('PRINT', 'PRINT'),
    'IF': Token('IF', 'IF'),
    'ELSE': Token('ELSE', 'ELSE'),
    'FUN' : Token('FUN', 'FUN'),
    'WHILE' : Token('WHILE', 'WHILE')
}

class AST(object):
    pass

class UnaryOp(AST):
    def __init__(self, op, expr):
        self.token = self.op = op
        self.expr = expr


class BinOp(AST):
    def __init__(self, left, op, right):
        self.left = left
        self.token = self.op = op
        self.right = right
    
    def __str__(self):
        return "BinOp({}, {}, {})".format(self.left, self.token, self.right)

    def __repr__(self):
        return self.__str__()

class Num(AST):
    def __init__(self, token):
        self.token = token
        self.value = token.value

    def __str__(self):
        return "Num({})".format(self.value)

    def __repr__(self):
        return self.__str__()
    


class Compound(AST):
    """Represents a 'BEGIN ... END' block"""
    def __init__(self):
        self.children = []


class Assign(AST):
    def __init__(self, left, op, right):
        self.left = left
        self.token = self.op = op
        self.right = right


class Print(AST):
    def __init__(self, op, right):
        self.token = self.op = op
        self.right = right

class If(AST):
    def __init__(self, op, condition, left, right):
        self.token = self.op = op
        self.condition = condition
        self.left = left # on True
        self.right = right # else-branch

class While(AST):
    def __init__(self, op, condition, statement):
        self.token = self.op = op
        self.condition = condition
        self.statement = statement

class Var(AST):
    """The Var node is constructed out of ID token."""
    def __init__(self, token):
        self.token = token
        self.value = token.value

    def __str__(self):
        return "Var({})".format(self.value)

    def __repr__(self):
        return self.__str__()

class FunDeclaration(AST):
    """ The function node """
    def __init__(self, token, fun_name, statement):
        self.token = token
        self.name = fun_name.value
        self.statement = statement

class FunName(AST):
    def __init__(self, token):
        self.token = token
        self.value = token.value

class FunInvoke(AST):
    def __init__(self, token, fun_name):
        self.token = token
        self.name = fun_name.value

class NoOp(AST):
    pass


