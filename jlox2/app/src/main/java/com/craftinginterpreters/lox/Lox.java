package com.craftinginterpreters.lox;

public class Lox {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        System.out.println(new Lox().getGreeting());
    }
}
