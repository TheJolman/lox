package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main entry point for the lox interpreter
 * Handles both file-based execution and an interactive REPL
 */
public class Lox {
  static boolean hadError = false;

  /**
   * Entry point for Lox interpreter
   * Handles command line args to either run a source file or start the REPL
   *
   * @param args Command line arguments. Can contain a single path to a Lox source
   *             file.
   * @throws IOException If there's an error reading the source file
   */
  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  /**
   * Reads entire Lox source file into memory and executes it.
   * Exits with code 65 in the case of an error.
   *
   * @param path Path to source file to execute
   * @throws IOException If there's an error reading the file
   */
  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));

    if (hadError)
      System.exit(65);
  }

  /**
   * Starts an interactive REPL session.
   * Repeatedly prompts for and executes lines of Lox code until EOF is reached.
   *
   * @throws IOException if there's an error reading from the prompt
   */
  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) {
      System.out.print("> ");
      String line = reader.readLine();
      if (line == null)
        break;
      run(line);
      hadError = false;
    }
  }

  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    // just print tokens for now
    for (Token token : tokens) {
      System.out.println(token);
    }
  }

  /** Reports an error at a specific line in the source code.
   *
   * @param line Line number where the error occurred
   * @param message Description of the error
   */
  static void error(int line, String message) {
    report(line, "", message);
  }

  private static void report(int line, String where, String message) {
    System.err.println("[" + line + "] Error" + where + ": " + message);
    hadError = true;
  }
}
