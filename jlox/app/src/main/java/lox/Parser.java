package lox;

import java.util.ArrayList;
import java.util.List;
import static lox.TokenType.*;

class Parser {
  private static class ParseError extends RuntimeException {
  }

  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  List<Stmt> parse() {
    List<Stmt> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(declaration());
    }
    return statements;
  }

  /**
   * Parses the expression grammar rule
   * expression -> equality
   */
  private Expr expression() {
    return assignment();
  }

  /**
   * Parses the declaration grammar rule
   * declaration -> varDecl | statement ;
   */
  private Stmt declaration() {
    try {
      if (match(VAR)) return varDeclaration();
      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  private Stmt statement() {
    if (match(PRINT)) return printStatement();

    return expressionStatement();
  }

  private Stmt printStatement() {
    Expr value = expression();
    consume(SEMICOLON, "Expectr ';' after value.");
    return new Stmt.Print(value);
  }

  /**
   * varDecl -> "var" IDENTIFIER ( "=" expression )? ";" ;
   */
  private Stmt varDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name.");

    Expr initializer = null;
    if (match(EQUAL)) {
      initializer = expression();
    }

    consume(SEMICOLON, "Expect ';' after variable declaration");
    return new Stmt.Var(name, initializer);
  }

  private Stmt expressionStatement() {
    Expr expr = expression();
    consume(SEMICOLON, "Expect ';' after expression.");
    return new Stmt.Expression(expr);
  }

  private Expr assignment() {
    Expr expr = equality();

    if (match(EQUAL)) {
      Token equals = previous();
      Expr value = assignment();

      if (expr instanceof Expr.Variable) {
        Token name = ((Expr.Variable)expr).name;
        return new Expr.Assign(name, value);
      }

      error(equals, "Invalid assignemtn target.");
    }
    return expr;
  }

  /**
   * Parses the equality grammar rule
   * equality -> comparison ( ( "!=" | "==" ) comparison )*
   */
  private Expr equality() {
    Expr expr = comparison();
    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Parses the comparison grammar rule
   * comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )*
   */
  private Expr comparison() {
    Expr expr = term();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expr right = term();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Addition and subtraction
   * term -> factor ( ( "-" | "+" ) factor )*
   */
  private Expr term() {
    Expr expr = factor();

    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expr right = factor();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Multiplication and division
   * factor -> unary ( ( "*" | "/" ) unary )
   */
  private Expr factor() {
    Expr expr = unary();

    while (match(SLASH, STAR)) {
      Token operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  /**
   * unary -> ( "!" | "-" ) unary
   * | primary
   */
  private Expr unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }
    return primary();
  }

  /**
   * primary -> NUMBER | STRING | "true" | "false" | "nil"
   * | "(" expression ")"
   */
  private Expr primary() {
    if (match(FALSE))
      return new Expr.Literal(false);
    if (match(TRUE))
      return new Expr.Literal(true);
    if (match(NIL))
      return new Expr.Literal(null);

    if (match(NUMBER, STRING)) {
      return new Expr.Literal(previous().literal);
    }

    if (match(IDENTIFIER)) {
      return new Expr.Variable(previous());
    }

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression");
      return new Expr.Grouping(expr);
    }

    throw error(peek(), "Expect expression.");
  }

  /**
   * Advances current token and returns true if it's any of the given types
   */
  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }
    return false;
  }

  private Token consume(TokenType type, String message) {
    if (check(type))
      return advance();

    throw error(peek(), message);
  }

  /**
   * Returns true if current token is the given type without advancing
   */
  private boolean check(TokenType type) {
    if (isAtEnd())
      return false;
    return peek().type == type;
  }

  /**
   * Consumes current token and returns it
   */
  private Token advance() {
    if (!isAtEnd())
      current++;
    return previous();
  }

  /**
   * Checks if there's any tokens left to parse
   */
  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  /**
   * Returns token we have yet to consume
   */
  private Token peek() {
    return tokens.get(current);
  }

  /**
   * Returns most recently consumed token
   */
  private Token previous() {
    return tokens.get(current - 1);
  }

  private ParseError error(Token token, String message) {
    Lox.error(token, message);
    return new ParseError();
  }

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == SEMICOLON)
        return;

      switch (peek().type) {
        case CLASS:
        case FOR:
        case FUN:
        case IF:
        case PRINT:
        case RETURN:
        case VAR:
        case WHILE:
          return;
        default:
          break;
      }
    }

    advance();
  }
}
