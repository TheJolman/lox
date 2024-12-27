package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
  final static String PROJECT_NAME = "jlox";

  public static void main(String args[]) {
    if (args.length != 0) {
      System.out.println(args + " takes no arguments.");
      System.exit(0);
    }
    System.out.println("This is project " + PROJECT_NAME + ".");
    Test.poop();
    System.exit(0);
  }
}
