package lox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class ScannerTest {

  @Test
  public void testEmptySource() {
    Scanner scanner = new Scanner("");
    List<Token> tokens = scanner.scanTokens();

    assertEquals(1, tokens.size());
    assertEquals(TokenType.EOF, tokens.get(0).type);
  }

  @Test
  public void testSingleCharacterTokens() {
    Scanner scanner = new Scanner("(){},.-+;*");
    List<Token> tokens = scanner.scanTokens();

    TokenType[] expectedTypes = {
      TokenType.LEFT_PAREN,
      TokenType.RIGHT_PAREN,
      TokenType.LEFT_BRACE,
      TokenType.RIGHT_BRACE,
      TokenType.COMMA,
      TokenType.DOT,
      TokenType.MINUS,
      TokenType.PLUS,
      TokenType.SEMICOLON,
      TokenType.STAR,
      TokenType.EOF,
    };

    assertEquals(expectedTypes.length, tokens.size());
    for (int i = 0; i < expectedTypes.length; ++i) {
      assertEquals(expectedTypes[i], tokens.get(i).type);
    }
  }

  @Test
  public void testOneOrTwoCharacterTokens() {
    Scanner scanner = new Scanner("! != = == > >= < <=");
    List<Token> tokens = scanner.scanTokens();

    TokenType[] expectedTypes = {
      TokenType.BANG,
      TokenType.BANG_EQUAL,
      TokenType.EQUAL,
      TokenType.EQUAL_EQUAL,
      TokenType.GREATER,
      TokenType.GREATER_EQUAL,
      TokenType.LESS,
      TokenType.LESS_EQUAL,
      TokenType.EOF,
    };

    assertEquals(expectedTypes.length, tokens.size());
    for (int i = 0; i < expectedTypes.length; ++i) {
      assertEquals(expectedTypes[i], tokens.get(i).type);
    }
  }

  @Test
  public void testString() {
    Scanner scanner = new Scanner("\"hi mom!\"");
    List<Token> tokens = scanner.scanTokens();

    assertEquals(2, tokens.size());
    assertEquals(TokenType.STRING, tokens.get(0).type);
    assertEquals("hi mom!", tokens.get(0).literal);
  }

  @Test
  public void testNumber() {
    Scanner scanner = new Scanner("123 123.7890");
    List<Token> tokens = scanner.scanTokens();

    assertEquals(3, tokens.size());
    assertEquals(TokenType.NUMBER, tokens.get(0).type);
    assertEquals(123.0, tokens.get(0).literal);
    assertEquals(TokenType.NUMBER, tokens.get(1).type);
    assertEquals(123.7890, tokens.get(1).literal);
  }

}
