from ast import *
class NodeVisitor(object):
    def visit(self, node):
        method_name = 'visit_' + type(node).__name__
        visitor = getattr(self, method_name, self.generic_visit)
        return visitor(node)

    def generic_visit(self, node):
        raise Exception('No visit_{} method'.format(type(node).__name__))

class Interpreter(NodeVisitor):
    GLOBAL_SCOPE = {}

    def __init__(self, parser):
        self.parser = parser

    def visit_BinOp(self, node):
        if node.op.type == PLUS:
            return self.visit(node.left) + self.visit(node.right)
        elif node.op.type == MINUS:
            return self.visit(node.left) - self.visit(node.right)
        elif node.op.type == MUL:
            if isinstance(node.left, Num):
                if node.left.value == 0:
                    #print("left zero")
                    return 0
            if isinstance(node.right, Num):
                if node.right.value == 0:
                    #print("right zero")
                    return 0 
            return self.visit(node.left) * self.visit(node.right)
        elif node.op.type == DIV:
            if isinstance(node.left, Num):
                if node.left.value == 0:
                    #print("left zero")
                    return 0
            return self.visit(node.left) / self.visit(node.right)

    def visit_Num(self, node):
        return node.value

    def visit_UnaryOp(self, node):
        op = node.op.type
        if op == PLUS:
            return +self.visit(node.expr)
        elif op == MINUS:
            return -self.visit(node.expr)

    def visit_Compound(self, node):
        for child in node.children:
            self.visit(child)

    def visit_Assign(self, node):
        var_name = node.left.value
        self.GLOBAL_SCOPE[var_name] = self.visit(node.right)

    def visit_Print(self, node):
        print(float(self.visit(node.right)))

    def visit_Var(self, node):
        var_name = node.value
        val = self.GLOBAL_SCOPE.get(var_name)
        if val is None:
            raise NameError(repr(var_name))
        else:
            return val

    def visit_If(self, node):
        condition = node.condition
        left = node.left
        right = node.right
        if self.visit(condition) != 0:
            self.visit(left)
        else:
            self.visit(right)
    
    def visit_While(self, node):
        condition = node.condition
        statement = node.statement

        while self.visit(condition) != 0:
            self.visit(statement)

    def visit_FunDeclaration(self, node):
        self.GLOBAL_SCOPE[node.name] = node.statement

    def visit_FunInvoke(self, node):
        self.visit(self.GLOBAL_SCOPE[node.name])

    def visit_NoOp(self, node):
        pass

    def interpret(self):
        tree = self.parser.parse()
        if tree is None:
            return ''
        return self.visit(tree)