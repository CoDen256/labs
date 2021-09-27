%%%%%%%%%%%%%%%%%%%%%% TOKENIZER %%%%%%%%%%%%%%%%%%%%%%%%

% list of unified char sequences are merged and converted to atoms representing tokens.
merge_tokens([], []).    
merge_tokens([[Char0|_]|UnifiedChars], Tokens):-
    whitespace(Char0), merge_tokens(UnifiedChars, Tokens).
merge_tokens([[Char0|CharSeq]|UnifiedChars], [Token|RemainingTokens]):-
    not(whitespace(Char0)), atom_chars(Token, [Char0|CharSeq]),
    merge_tokens(UnifiedChars, RemainingTokens).
   

% unify sequences of chars to multiple lists depending on the type of char
% alphanumeric chars are unified in one list
% non alphanumerics are not unified with other chars like whitespace or '{'
unify_sequences([A], [[A]]).
unify_sequences([NextChar|Source],  NextTokens):-
    unify_sequences(Source, [PrevToken|PrevTokens]),
    process_next_char(NextChar, PrevToken, PrevTokens, NextTokens).
 
process_next_char(NextChar, [PrevChar|CurrentToken], Tokens, [[NextChar,PrevChar|CurrentToken]|Tokens]):-
    alphanumeric(NextChar), alphanumeric(PrevChar).
process_next_char(NextChar, [PrevChar|CurrentToken], Tokens, [[NextChar],[PrevChar|CurrentToken]|Tokens]):-
    alphanumeric(NextChar), not(alphanumeric(PrevChar)).
process_next_char(NextChar, [PrevChar|CurrentToken], Tokens, [[NextChar],[PrevChar|CurrentToken]|Tokens]):-
    not(alphanumeric(NextChar)).

alphanumeric(Char):-alpha(Char); numeric(Char).
whitespace(Char):-char_code(Char, N),N =< 32.    
numeric(Char):- char_code(Char, N), N =< 57, N >= 48.
alpha(Char):- char_code(Char, N), (   (   N =< 90, N >= 65) ; (   N =< 122, N >= 97)).  

% convert list of chars to list of tokens
tokenize(Source, Tokens):-
    unify_sequences(Source, Sequences),
    merge_tokens(Sequences, Tokens).

% [int, main, '(', int, input, ')', (=), '3', (+), input]
%%%%%%%%%%%%%%%%%%%%%% LEXER %%%%%%%%%%%%%%%%%%%%%%%%

% mapping of tokens to their lexeme.
lex_token('=',    'ASSIGN').
lex_token(if,   'IF').
lex_token(else, 'ELSE').
lex_token(print, 'PRINT').
lex_token('+',    'PLUS').
lex_token('-',    'MINUS').
lex_token('*',    'MUL').
lex_token('/',    'DIV').
lex_token('%',    'MOD').
lex_token('(',    'OPEN_P').
lex_token(')',    'CLOSE_P').
lex_token('{',    'OPEN_B').
lex_token('}',    'CLOSE_B').
lex_token(';',    'SEMI').

lex_token(Number, 'INTEGER') :-
  atom_number(Number, Integer),
  integer(Integer).
lex_token(_, 'ID'). % any other atoms are not reserved words, so they are identificators

%lex(X):-lex(X, _). %
lexer([], []).
lexer([Token|TokenList], [lex(Token, Lexeme)|LexedList]):-
    lex_token(Token,Lexeme),
    lexer(TokenList, LexedList).


% ['TYPE_INT', 'ID', 'OPEN_P', 'TYPE_INT', 'ID', 'CLOSE_P', 'ASSIGN', 'INTEGER', 'ARITH_ADD', 'ID']


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PARSER %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Grammar
% statement_list : statement (SEMI statement_list)
% 
% statement : assignment_statement
%             | if_else_statement
%             | print_statement
%                  
% if_else_statement : IF OPEN_P expr CLOSE_P OPEN statement_list CLOSE 
%                     (ELSE OPEN statement_list CLOSE)
%                             
% print_statement : PRINT expr                            
% assignment_statement : variable ASSIGN expr                            
%                             
% variable : ID                            
% expr   : term ((PLUS | MINUS) expr)                            
% term 	 : factor ((MUL | DIV) term)                            
% factor : PLUS  factor
%         | MINUS factor
%         | INTEGER
%         | OPEN_P expr CLOSE_P
%         | variable                            


parser(LexedList, ParsedList):-
    program(ParsedList, LexedList, []).

program(program(Program), A, B):- 
    statement_list(Program, A, B).

statement_list(list(Statement), A,B):-
    statement(Statement, A, B).

statement_list(list(Statement, StatementList),A,C):-
    statement(Statement, A,B), 
    eat('SEMI', B, B0),
    statement_list(StatementList, B0,C).

statement(stmt(P),A,B):-
    assignment_statement(P, A,B);
    if_else_statement(P,A,B);
    print_statement(P, A, B).
    
print_statement(print(Expression), A, B):-
    eat('PRINT', A, B0),
    expr(Expression, B0, B).

if_else_statement(if_stmt(Expression, TrueStatementList),A,G):-
    eat(['IF', 'OPEN_P'], A, B),
    expr(Expression, B, C),
    eat(['CLOSE_P', 'OPEN_B'], C, D),
    statement_list(TrueStatementList, D,F), 
    eat('CLOSE_B', F, G).

if_else_statement(if_else_stmt(Expression, TrueStatementList, FalseStatementList),A,K):-
    eat(['IF', 'OPEN_P'], A, B),
    expr(Expression, B, C),
    eat(['CLOSE_P', 'OPEN_B'], C, D),
    statement_list(TrueStatementList, D,F), 
    eat(['CLOSE_B', 'ELSE', 'OPEN_B'], F, G),
    statement_list(FalseStatementList, G, H), 
    eat('CLOSE_B', H, K).

assignment_statement(assign_stmt(Variable, Expression),A, D):- 
    variable(Variable, A, B),
    eat('ASSIGN', B, C),
    expr(Expression, C, D).

expr(expr(P), A, B):-term(P,A,B).
expr(expr(Term, Operator, Expression),A,D):-
    term(Term, A,B), 
    operator_plus(Operator, B, C),
    expr(Expression,C,D).

term(term(P),A,B):-factor(P,A,B).
term(term(Factor, Operator, Term),A,D):-
    factor(Factor,A,B), 
    operator_mul(Operator, B, C),
    term(Term,C,D).

factor(factor(Sign, Factor),A,C):-
    operator_plus(Sign, A, B),
    factor(Factor,B,C).

factor(factor(Expression),A,D):-
    eat('OPEN_P', A, B),
    expr(Expression, B, C), 
    eat('CLOSE_P', C, D).
factor(factor(Integer),A,B):-
    integer(Integer, A, B).

factor(factor(Variable),A,B):-
    variable(Variable,A,B).

integer(int(IntegerValue), [lex(IntegerValue, 'INTEGER')|A], A).

variable(var(VariableName), [lex(VariableName, 'ID')|A], A).

operator_mul(operator('MUL'), [lex(_, 'MUL')|A], A).
operator_mul(operator('DIV'), [lex(_, 'DIV')|A], A).
operator_plus(operator('PLUS'), [lex(_, 'PLUS')|A], A).
operator_plus(operator('MINUS'), [lex(_, 'MINUS')|A], A).


eat(TOKEN_LIST, CURRENT, NEXT):-
    is_list(TOKEN_LIST),
    as_lex(TOKEN_LIST, LEX_LIST),
    append(LEX_LIST,NEXT,CURRENT).
eat(TOKEN, CURRENT, NEXT):-
    not(is_list(TOKEN)),
    eat([TOKEN], CURRENT, NEXT).
as_lex([Token|A], [lex(_, Token)|B]):-as_lex(A, B).
as_lex([], []).    
                           
                        
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

handle_stmt(stmt(P), Scope, NewScope):-
    handle_assign_stmt(P, Scope, NewScope);
    handle_print_stmt(P, Scope), clone_variables(Scope, NewScope).

handle_assign_stmt(assign_stmt(var(VariableName), Expression), Scope, NewScope):-
    handle_expr(Expression, Value, Scope),
    setVariable(Scope, VariableName, Value, NewScope).

handle_print_stmt(print(Expression), Scope):-
    handle_expr(Expression, Value, Scope), 
    write(Value),nl.

handle_expr(expr(P), Value, Scope):-handle_term(P, Value, Scope).
handle_expr(expr(Term, operator(Operator), Expression), Value, Scope):-
    handle_term(Term, Value1, Scope), 
    handle_expr(Expression, Value2, Scope),
    evaluate(Value1, Operator, Value2, Value).

handle_term(term(P), Value, Scope):-handle_factor(P, Value, Scope).
handle_term(term(Factor, operator(Operator), Term), Value, Scope):-
    handle_factor(Factor, Value1, Scope),
    handle_term(Term, Value2, Scope),
    evaluate(Value1, Operator, Value2, Value).

handle_factor(factor(operator('MINUS'), Factor), Value, Scope):-
    handle_factor(Factor, Value1, Scope),
    Value is Value1 - Value1 * 2. % negate
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
    Result is div(Value1, Value2).

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

%
%  [int, main, '(', int, input, ')', (=), '3', (+), input]
%  ['TYPE_INT', 'ID', 'OPEN_P', 'TYPE_INT', 'ID', 'CLOSE_P', 'ASSIGN', 'INTEGER', 'ARITH_ADD', 'ID']
%  
% [[[int, id], '(', [[int, id], []], ')', (=), [[integer], [[(+), [id, []]]]]], []]
% [[[int, main], '(', [[int, input], []], ')', (=), [['3'], [[(+), [input, []]]]]], []]

% a = 1; c = a + 2 * 3; print c
% [lex(a, 'ID'), lex((=), 'ASSIGN'), lex('1', 'INTEGER'), lex((;), 'SEMI'), lex(c, 'ID'), lex((=), 'ASSIGN'), 
% lex(a, 'ID'), lex((+), 'PLUS'), lex('2', 'INTEGER'), lex((*), 'MUL'), lex('3', 'INTEGER')]
% 
% [[lex(a, 'ID'), lex((=), 'ASSIGN'), [lex('1', 'INTEGER')]], lex((;), 'SEMI'), [lex(c, 'ID'), lex((=), 'ASSIGN'), 
% [[lex(a, 'ID')], lex((+), 'PLUS'), [[lex('2', 'INTEGER')], lex((*), 'MUL'), [lex('3', 'INTEGER')]]]]]
% 
% 
% 
% 
% [lex(a, 'ID'), lex((=), 'ASSIGN'), lex('1', 'INTEGER'), lex((;), 'SEMI'), lex(c, 'ID'), lex((=), 'ASSIGN'), lex(a, 'ID'), lex((+), 'PLUS'), lex('2', 'INTEGER'),
%  lex((*), 'MUL'), lex('3', 'INTEGER'), lex((;), 'SEMI'), lex(print, 'PRINT'), lex(c, 'ID')]
% 
% input string is converted to list of words(group of chars) and then to
% tokens(the same as prolog atoms). 
% Then each token is converted to lexeme, defining meaning of the token.
interpret(Source, AbstractSyntaxTree) :-
    string_chars(Source, SourceList), % convert input to list of chars
    tokenize(SourceList, TokenList),
    lexer(TokenList, LexedList), !,
    parser(LexedList, AbstractSyntaxTree),
    interpreter(AbstractSyntaxTree), !.
%% maybe shorten it somehow the PARSING???? AND LEXER ???
%%% MAYBE INSTAD OF LISTS MAKE MEAININGFUL FUNCTORS????                  
% stop lexer generating lists replacing everything with ID