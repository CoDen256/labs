% interpret("
%            a = 1; 
%            b = 2;
%            c = a - b*2;
%            d = a * 4 / 10 * 10 - 10 * 2;
%            print a;
%            print b;
%            print c;
%            print d;
%            if (a-a*4 <= c){
%            		print 99999
%            };
%            e = 100;
%            while (e > 10){
%           		if (e == 50){
%            			print 88888
%            		}else{
%            			print e
%            		};
%            		e = e - 10
%            }
%            ")
%

interpret(Source, LexedList, TokenList,  AbstractSyntaxTree) :-
    string_chars(Source, SourceList), 			% convert input to list of chars
    lexer(SourceList, LexedList),				% convert list of chars to lexemes
    tokenizer(LexedList, TokenList),!,			% convert lexemes to corresponding tokens
    parser(TokenList, AbstractSyntaxTree),		% parse tokens using grammar and build Abstract Syntax Tree 
    interpreter(AbstractSyntaxTree), !.			% interpret Abstract Syntax Tree
interpret(Source):- interpret(Source, _, _, _).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% LEXER %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Converts list of chars to list of lexemes(atoms) 
% 
% In: 	'a = 1; print (12 + a)' 
% Out: 	[a, (=), '1', (;), print, '(', '12', (+), a, ')']
% 
% lexer(+Source, -Tokens).
lexer(Source, Tokens):-
    unify_sequences(Source, Sequences),
    join_lexemes(Sequences, Tokens).

% Unify sequences of chars to groups.
% In: 	'print (12+a)' 
% Out: 	[[p,r,i,n,t], [' '], ['('], [1,2], [+], [a], [)]]
unify_sequences([A], [[A]]).
unify_sequences([NextChar|Source],  NextTokens):-
    unify_sequences(Source, [PrevToken|PrevTokens]),
    process_next_char(NextChar, PrevToken, PrevTokens, NextTokens).
 
process_next_char(NextChar, [PrevChar|CurrentToken], Tokens, [[NextChar,PrevChar|CurrentToken]|Tokens]):-
    can_be_unified(NextChar, PrevChar).
process_next_char(NextChar, [PrevChar|CurrentToken], Tokens, [[NextChar],[PrevChar|CurrentToken]|Tokens]):-
    cannot_be_unified(NextChar, PrevChar).

can_be_unified(Char0, Char1):-
    alphanumeric(Char0), alphanumeric(Char1).
can_be_unified('=', '=').
can_be_unified('<', '=').
can_be_unified('>', '=').
can_be_unified('!', '=').
cannot_be_unified(Char0, Char1):-
    alphanumeric(Char0), not(alphanumeric(Char1));
    not(alphanumeric(Char0)), alphanumeric(Char1);
    not(alphanumeric(Char0)),not(alphanumeric(Char1)).

alphanumeric(Char):-alpha(Char); numeric(Char).
whitespace(Char):-char_code(Char, N), N =< 32.    
numeric(Char):- char_code(Char, N), N =< 57, N >= 48.
alpha(Char):- char_code(Char, N), (   (   N =< 90, N >= 65) ; (   N =< 122, N >= 97)).  

% Join lists of chars to atoms(lexemes) and skip whitespaces
% In : 		[[p,r,i,n,t], [' '], ['('], [1,2], [+], [a], [)]]
% Out: 		[print, '(', '12', (+), a, ')']
join_lexemes([], []).    
join_lexemes([[Char0|_]|UnifiedChars], Tokens):-
    whitespace(Char0), join_lexemes(UnifiedChars, Tokens). 				% skip whitespace
join_lexemes([[Char0|CharSeq]|UnifiedChars], [Token|RemainingTokens]):-
    not(whitespace(Char0)), atom_chars(Token, [Char0|CharSeq]),			% join to atom
    join_lexemes(UnifiedChars, RemainingTokens).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TOKENIZER %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Converts list of lexemes to list of corresponding tokens
%
% In: 	[a, (=), '1', (;)]
% Out:  [t(a, 'ID'), t((=), 'ASSIGN'), t('1', 'INTEGER'), t((;), 'SEMI')],
% 
% tokenizer(+Lexemes, -Tokens)
tokenizer([], []).
tokenizer([Lexeme|Lexemes], [t(Lexeme, Token)|Tokens]):-
    lex_token(Lexeme,Token),
    tokenizer(Lexemes, Tokens).

% lex_token(Lexeme, Token) - mapping of a lexeme to its name(category).
lex_token('=',    'ASSIGN').
lex_token(if,     'IF').
lex_token(else,   'ELSE').
lex_token(print,  'PRINT').
lex_token(while,  'WHILE').
lex_token('+',    'PLUS').
lex_token('-',    'MINUS').
lex_token('*',    'MUL').
lex_token('/',    'DIV').
lex_token('(',    'OPEN_P').
lex_token(')',    'CLOSE_P').
lex_token('{',    'OPEN_B').
lex_token('}',    'CLOSE_B').
lex_token(';',    'SEMI').

lex_token('==',    'EQ').
lex_token('!=',    'NE').
lex_token('>=',    'GE').
lex_token('<=',    'LE').
lex_token('>',     'GT').
lex_token('<',     'LT').

lex_token(Number, 'INTEGER') :-
  atom_number(Number, Integer),
  integer(Integer).
lex_token(_, 'ID'). % any other atoms are not reserved words, so they are identificators


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PARSER %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Grammar
%
% program: statement_list
% 
% statement_list : statement 
% 				   | statement SEMI
% 				   | statement SEMI statement_list
% 
% statement : assignment_statement
%             | if_else_statement
%             | print_statement
%             | while_statement
%                  
% if_else_statement : IF OPEN_P bool CLOSE_P OPEN_B statement_list CLOSE_B 
%                     (ELSE OPEN statement_list CLOSE)
% while_statement : WHILE OPEN_P bool CLOSE_P OPEN_B statement_list CLOSE_B
%                             
% print_statement : PRINT expr                            
% assignment_statement : variable ASSIGN expr                            
%                             
% variable : ID
% 
% bool   : expr operator_bool expr    
% operator_bool: EQ | NE | GE | LE | GT | LT
%  									                                             
% expr   : term sum             		'expr: expr (PLUS|MINUS) term' - left-recursive   								
% sum    : (PLUS | MINUS) term sum
%      	 | <empty>   
%                 
% term   : factor product
% product: (MUL | DIV) factor product
%        | <empty>      
%                            
% factor : (PLUS | MINUS)  factor
%         | INTEGER
%         | OPEN_P expr CLOSE_P
%         | variable                            


% Parses a given token list and creates a parsed tree, which is a AbstractSyntaxTree
% 
% Source :	'a = 1 - 2 * 3;'
% In: 		[t(a, 'ID'), t((=), 'ASSIGN'), t('1', 'INTEGER'), t((-), 'MINUS'), t('2', 'INTEGER'), t((*), 'MUL'), t('3', 'INTEGER'), t((;), 'SEMI')],
% Out: 
% program(
% 	list(
% 		stmt(
% 			assign_stmt(
% 				var(a), 
% 				expr(
% 					 term(
% 					 	factor(int('1')), 
% 					 	product()),
% 					 sum(
% 					 	operator('MINUS'), 
% 					 	term(
% 					 		factor(int('2')), 
% 					 		product(
% 					 			operator('MUL'), 
% 					 			factor(int('3')), 
% 					 			product())
% 					 	), 
% 					 	sum()))))))
% 					 	
% parser(+TokenList, -ParsedList)
parser(TokenList, ParsedList):-
    program(ParsedList, TokenList, []).
  % program(-Program, +CurrentTokens, +NextTokens).

% program: statement_list
program(program(Program), A, B):- 
    statement_list(Program, A, B). 

% statement_list : statement (SEMI)
statement_list(list(Statement), A,B):-
    statement(Statement, A, B0),
    (  skip('SEMI', B0, B); clone_variables(B0, B)).

% statement_list : statement SEMI statement_list
statement_list(list(Statement, StatementList),A,C):-
    statement(Statement, A,B), 
    skip('SEMI', B, B0),
    statement_list(StatementList, B0,C).

% statement : assignment_statement | if_else_statement | print_statement | while_statement
statement(stmt(Statement),A,B):-
    assignment_statement(Statement, A, B);
    if_else_statement(Statement, A, B);
    while_statement(Statement, A, B);
    print_statement(Statement, A, B).
 
% print_statement : PRINT expr  
print_statement(print(Expression), A, B):-
    skip('PRINT', A, B0),
    expr(Expression, B0, B).

% if_else_statement : IF OPEN_P bool CLOSE_P OPEN_B statement_list CLOSE_B 
if_else_statement(if_stmt(BoolExpression, TrueStatementList),A,G):-
    skip(['IF', 'OPEN_P'], A, B),
    bool_expr(BoolExpression, B, C),
    skip(['CLOSE_P', 'OPEN_B'], C, D),
    statement_list(TrueStatementList, D,F), 
    skip('CLOSE_B', F, G).

% if_else_statement : IF OPEN_P bool CLOSE_P OPEN_B statement_list CLOSE_B 
% 					  ELSE OPEN statement_list CLOSE
if_else_statement(if_stmt(BoolExpression, TrueStatementList, FalseStatementList),A,K):-
    skip(['IF', 'OPEN_P'], A, B),
    bool_expr(BoolExpression, B, C),
    skip(['CLOSE_P', 'OPEN_B'], C, D),
    statement_list(TrueStatementList, D,F), 
    skip(['CLOSE_B', 'ELSE', 'OPEN_B'], F, G),
    statement_list(FalseStatementList, G, H), 
    skip('CLOSE_B', H, K).

% while_statement : WHILE OPEN_P bool CLOSE_P OPEN_B statement_list CLOSE_B
while_statement(while_stmt(BoolExpression, TrueStatementList), A, G):-
    skip(['WHILE', 'OPEN_P'], A, B),
    bool_expr(BoolExpression, B, C),
    skip(['CLOSE_P', 'OPEN_B'], C, D),
    statement_list(TrueStatementList, D, F),
    skip('CLOSE_B', F, G).

% assignment_statement : variable ASSIGN expr     
assignment_statement(assign_stmt(Variable, Expression),A, D):- 
    variable(Variable, A, B),
    skip('ASSIGN', B, C),
    expr(Expression, C, D).

% bool: expr operator_bool expr   
bool_expr(bool(ExpressionA, BoolOperator, ExpressionB), A, D):-
    expr(ExpressionA, A, B),
    operator_bool(BoolOperator, B, C),
    expr(ExpressionB, C, D).
         

% expr: term sum  								
expr(expr(Term, Sum),A,C):-
    term(Term, A, B), 
    sum(Sum, B, C).

% sum: (PLUS | MINUS) term sum 
sum(sum(Operator, Term, Sum),A,D):-
    operator_sum(Operator, A, B),
    term(Term, B, C),
    sum(Sum, C, D).
% sum: <empty>
sum(sum(), A, A).


% term   : factor product
term(term(Factor, Product),A,C):-
    factor(Factor,A,B),
    product(Product, B, C).

% product: (MUL | DIV) factor product
product(product(Operator, Factor, Product), A, D):-
    operator_product(Operator, A, B),
    factor(Factor,B,C),
    product(Product, C, D).
% product:  <empty>   
product(product(), A, A).                   

% factor : (PLUS | MINUS)  factor
factor(factor(Sign, Factor),A,C):-
    operator_sum(Sign, A, B),
    factor(Factor,B,C).

% factor: OPEN_P expr CLOSE_P 
factor(factor(Expression),A,D):-
    skip('OPEN_P', A, B),
    expr(Expression, B, C), 
    skip('CLOSE_P', C, D).
% factor: INTEGER
factor(factor(Integer),A,B):-
    integer(Integer, A, B).
% factor: variable
factor(factor(Variable),A,B):-
    variable(Variable,A,B).


% variable : ID
% 			-TreeNode		   +CurrentToken          +RestTokens
variable(var(VariableName), [t(VariableName, 'ID')|A], A).
integer(int(IntegerValue), [t(IntegerValue, 'INTEGER')|A], A).

% operator_product: MUL|DIV
operator_product(operator('MUL'), [t(_, 'MUL')|A], A).
operator_product(operator('DIV'), [t(_, 'DIV')|A], A).
% operator_sum: PLUS|MINUS
operator_sum(operator('PLUS'), [t(_, 'PLUS')|A], A).
operator_sum(operator('MINUS'), [t(_, 'MINUS')|A], A).

% operator_bool: EQ | NE | GE | LE | GT | LT
operator_bool(bool_operator('EQ'), [t(_, 'EQ')|A], A).
operator_bool(bool_operator('NE'), [t(_, 'NE')|A], A).
operator_bool(bool_operator('GE'), [t(_, 'GE')|A], A).
operator_bool(bool_operator('LE'), [t(_, 'LE')|A], A).
operator_bool(bool_operator('GT'), [t(_, 'GT')|A], A).
operator_bool(bool_operator('LT'), [t(_, 'LT')|A], A).

% Is used to skip given token name. The Tree Node is not needed but the next tokens have to be processed properly.
% skip(+TokenNames, +CurrenTokens, -NextTokens) 
skip(TokenNames, CurrenTokens, NextTokens):-
    is_list(TokenNames),
    as_tokens(TokenNames, Tokens),
    append(Tokens,NextTokens,CurrenTokens).
% Same as skip but only for one Token 
skip(TokenName, CurrenTokens, NextTokens):-
    not(is_list(TokenName)),
    skip([TokenName], CurrenTokens, NextTokens).

% Converts list of tokens names to list of tokens
% In: ['SEMI', 'OPEN_P']
% Out: [t(';', 'SEMI'), t('(', 'OPEN_P')]
% as_tokens(+TokenNames, -Tokens)
as_tokens([Token|A], [t(_, Token)|B]):-as_tokens(A, B).
as_tokens([], []).    
                           
                        
%%%%%%%%%%%%%%%%%%%%%%%%%%%%% INTERPRETER %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
interpreter(AST):-
    handle_program(AST, [], _).

handle_program(program(StatementList), Scope, NewScope):-
    handle_list(StatementList, Scope, NewScope).

handle_list(list(Statement), Scope, NewScope):-
    handle_stmt(Statement, Scope, NewScope).
handle_list(list(Statement, StatementList), Scope, NewScope):-
    handle_stmt(Statement, Scope, Scope1),
    handle_list(StatementList, Scope1, NewScope).
%%% Statements %%%
handle_stmt(stmt(Statement), Scope, NewScope):-
    handle_assign_stmt(Statement, Scope, NewScope);
    handle_print_stmt(Statement, Scope), clone_variables(Scope, NewScope);
    handle_if_statement(Statement, Scope, NewScope);
    handle_while_statement(Statement, Scope, NewScope).

handle_assign_stmt(assign_stmt(var(VariableName), Expression), Scope, NewScope):-
    handle_expr(Expression, Value, Scope),
    setVariable(Scope, VariableName, Value, NewScope).

handle_print_stmt(print(Expression), Scope):-
    handle_expr(Expression, Value, Scope), 
    write(Value),nl.

handle_if_statement(if_stmt(BoolExpression, TrueStatementList), Scope, NewScope):-
    handle_bool_expr(BoolExpression, Scope, BoolValue),
    ( 
      BoolValue == 1, handle_list(TrueStatementList, Scope, NewScope);
      BoolValue == 0, clone_variables(Scope, NewScope)
    ).
handle_if_statement(if_stmt(BoolExpression, TrueStatementList, FalseStatementList), Scope, NewScope):-
    handle_bool_expr(BoolExpression, Scope, BoolValue),
     ( 
      BoolValue == 1, handle_list(TrueStatementList, Scope, NewScope);
      BoolValue == 0, handle_list(FalseStatementList, Scope, NewScope)
    ).
   
handle_while_statement(while_stmt(BoolExpression, TrueStatementList), Scope, NewScope):-
    handle_bool_expr(BoolExpression, Scope, BoolValue),
    ( 
      BoolValue == 1, handle_list(TrueStatementList, Scope, Scope0),
              handle_while_statement(while_stmt(BoolExpression, TrueStatementList), Scope0, NewScope);
      BoolValue == 0, clone_variables(Scope, NewScope)
    ).
%%% Expression, Term, Factor handlers %%%
handle_bool_expr(bool(Expression1, bool_operator(BoolOperator), Expression2), Scope, BoolValue):-
    handle_expr(Expression1, Value1, Scope),
    handle_expr(Expression2, Value2, Scope),
    evaluate_bool(Value1, BoolOperator, Value2, BoolValue).


% EXPRESSIONS %
handle_expr(expr(Term, Sum), Value, Scope):-
    handle_term(Term, Value1, Scope), 
    handle_sum(Sum, Value2, Scope),
    Value is Value1 + Value2.

handle_sum(sum(operator(Operator), Term, TermSum), Value, Scope):-
    handle_term(Term, Value1, Scope),
    handle_sum(TermSum, Value2, Scope),
    evaluate(Value2, Operator, Value1, Value).
handle_sum(sum(), 0, _Scope).

% TERMS %
handle_term(term(Factor, Product), Value, Scope):-
    handle_factor(Factor, Value1, Scope),
    handle_product(Product, Value2, Scope),
    Value is Value1 * Value2.

handle_product(product(operator(Operator), Factor, Product), Value, Scope):-
    handle_factor(Factor, Value1, Scope),
    handle_product(Product, Value2, Scope),
    evaluate(Value2, Operator, Value1, Value).
handle_product(product(), 1, _Scope).


% FACTORS %
handle_factor(factor(operator('MINUS'), Factor), Value, Scope):-
    handle_factor(Factor, Value1, Scope),
    Value is - Value1. % negate
handle_factor(factor(operator('PLUS'), Factor), Value, Scope):-
    handle_factor(Factor, Value, Scope).


handle_factor(factor(Expression), Value, Scope):-
    handle_expr(Expression, Value, Scope).
handle_factor(factor(Integer), Value, _):-
    handle_integer(Integer, Value).

handle_factor(factor(Variable), Value, Scope):-
    handle_variable(Variable, Value, Scope).

handle_integer(int(IntegerValue), Value):-
    atom_number(IntegerValue, Value).

handle_variable(var(VariableName), Value, Scope):-
    get_variable(Scope, VariableName, Value).

evaluate(Value1, 'PLUS', Value2, Result):-
    Result is Value1 + Value2.
evaluate(Value1, 'MINUS', Value2, Result):-
    Result is Value1 - Value2.
evaluate(Value1, 'MUL', Value2, Result):-
    Result is Value1 * Value2.
evaluate(Value1, 'DIV', Value2, Result):-
    Result is Value1 / Value2.

evaluate_bool(Value1, 'EQ', Value1, 1).
evaluate_bool(Value1, 'NE', Value2, 1):-not(Value1 is Value2). 
evaluate_bool(Value1, 'GT', Value2, 1):-Value1 > Value2.
evaluate_bool(Value1, 'GE', Value2, 1):-Value1 >= Value2.
evaluate_bool(Value1, 'LT', Value2, 1):-Value1 < Value2.
evaluate_bool(Value1, 'LE', Value2, 1):-Value1 =< Value2.
evaluate_bool(_, _, _, 0).
            
%%%%% Variables %%%%%%%
setVariable([], Variable, NewScopeValue, [entry(Variable, NewScopeValue)]).
setVariable([entry(Variable, _)|VariableList], Variable, NewScopeValue, [entry(Variable, NewScopeValue)|VariableList]).
setVariable([A|VariableList], Variable, NewScopeValue, [A|NewScopeVariableList]):-
    setVariable(VariableList, Variable, NewScopeValue, NewScopeVariableList).
    

get_variable([entry(Variable, Value)|_], Variable, Value).
get_variable([_|VariableList], Variable, Value):-
    get_variable(VariableList, Variable, Value).
get_variable([], VariableName, 0):-
    write("Variable not defined: "),
    write(VariableName), nl, 
    throw(error(syntax_error(var(VariableName)), 0)).

clone_variables(X, X).

