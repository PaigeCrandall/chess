package client;

import chess.*;

import java.io.PrintStream;

public class ClientMain {
    public static void main(String[] args) {
        Repl repl = new Repl();
        repl.runInteractions(System.out);
    }
}