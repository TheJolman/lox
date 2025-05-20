package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

/**
 * Scanner performs lexical analysis on source code, converting raw text into a
 * sequence
 * of tokens. Tracks line numbers for error reporting.
 */
class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("and", AND);
    keywords.put("class", CLASS);
    keywords.put("else", ELSE);
    keywords.put("false", FALSE);
    keywords.put("for", FOR);
    keywords.put("fun", FUN);
    keywords.put("if", IF);
    keywords.put("nil", NIL);
    keywords.put("or", OR);
    keywords.put("print", PRINT);
    keywords.put("return", RETURN);
    keywords.put("super", SUPER);
    keywords.put("this", THIS);
    keywords.put("true", TRUE);
    keywords.put("var", VAR);
    keywords.put("while", WHILE);
  }

  Scanner(String source) {
    this.source = source;
  }

  /**
   * Scans all tokens in the source code.
   *
   * @return List of tokens found in the source
   */
  List<Token> scanTokens() {
    while (!isAtEnd()) {
      // at beginning of next lexeme
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  /**
   * Scans a single token from the current position in the source code.
   * Handles single-char tokens, two char tokens, strings, numbers, and
   * whitespace.
   */
  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(':
        addToken(LEFT_PAREN);
        break;
      case ')':
        addToken(RIGHT_PAREN);
        break;
      case '{':
        addToken(LEFT_BRACE);
        break;
      case '}':
        addToken(RIGHT_BRACE);
        break;
      case ',':
        addToken(COMMA);
        break;
      case '.':
        addToken(DOT);
        break;
      case '+':
        addToken(PLUS);
        break;
      case '-':
        addToken(MINUS);
        break;
      case ';':
        addToken(SEMICOLON);
        break;
      case '*':
        addToken(STAR);
        break;
      case '!':
        addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;
      case '/':
        if (match('/')) {
          while (peek() != '\n' && !isAtEnd()) { // single-line comments
            advance();
          }
        } else if (match('*')) { // multi-line comments
          boolean foundClosing = false;
          while (!isAtEnd()) {
            if (peek() == '\n') {
              line++;
            }
            if (peek() == '*' && peekNext() == '/') {
              // NOTE: Should I just just use current += 2 here instead? And current++ for
              // single-line?
              advance();
              advance();
              foundClosing = true;
              break;
            }
            advance();
          }
          if (isAtEnd() && !foundClosing) {
            Lox.error(line, "Unterminated multi-line comment.");
          }
        } else { // division
          addToken(SLASH);
        }
        break;

      case ' ':
      case '\r':
      case '\t':
        break;
      case '\n':
        line++;
        break;

      case '"':
        string();
        break;

      default:
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          Lox.error(line, "Unexpected character.");
        }
        break;
    }
  }

  /**
   * Consumes rest of identifier or reserved word.
   */
  private void identifier() {
    while (isAlphaNumeric(peek()))
      advance();

    String text = source.substring(start, current);
    TokenType type = keywords.get(text);
    if (type == null)
      type = IDENTIFIER;
    addToken(type);
  }

  /**
   * Consumes rest of number literal.
   */
  private void number() {
    while (isDigit(peek()))
      advance();

    // Look for fractional part of number.
    if (peek() == '.' && isDigit(peekNext())) {
      // Consume the "."
      advance();
    }

    while (isDigit(peek()))
      advance();

    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  /**
   * Consumes string literal.
   * Multi-line strings OK
   */
  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n')
        line++;
      advance();
    }

    if (isAtEnd()) {
      Lox.error(line, "Unterminated string.");
      return;
    }

    advance(); // The closing ".

    // Trim the surrounding quotes.
    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  /**
   * Checks if the next char matches the expected char.
   * Used for two-char tokens.
   *
   * @param expected The character to match
   * @return true if the next character matches, false otherwise
   */
  private boolean match(char expected) {
    if (isAtEnd())
      return false;
    if (source.charAt(current) != expected)
      return false;

    current++;
    return true;
  }

  /**
   * Returns the next character without advancing the scanner.
   *
   * @return The next character or '\0' if at end of source
   */
  private char peek() {
    if (isAtEnd())
      return '\0';
    return source.charAt(current);
  }

  /**
   * Returns the character two ahead without advancing the scanner.
   *
   * @return The next next character or '\0' if at end of source
   */
  private char peekNext() {
    if (current + 1 >= source.length())
      return '\0';
    return source.charAt(current + 1);
  }

  /**
   * Checks if 'c' is [a-zA-Z_]
   */
  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
        (c >= 'A' && c <= 'Z') ||
        c == '_';
  }

  /**
   * Checks if 'c' is [a-zA-Z_0-9]
   */
  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  /**
   * Checks if 'c' is [0-9]
   */
  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  /**
   * Checks if the scanner is at the end of the source string.
   */
  private boolean isAtEnd() {
    return current >= source.length();
  }

  /**
   * Consumes the current character and returns it.
   *
   * @return The current character
   */
  private char advance() {
    return source.charAt(current++);
  }

  /**
   * Adds a token with no literal value.
   *
   * @param type The type of token to add
   */
  private void addToken(TokenType type) {
    addToken(type, null);
  }

  /**
   * Adds a token with an associated literal value.
   *
   * @param type    The type of token to add
   * @param literal The literal value associated with the token
   */
  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

}
