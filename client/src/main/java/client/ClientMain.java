package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) {

        String serverUrl = (args.length > 0) ? args[0] : "http://localhost:8080";

        Repl repl = new Repl(serverUrl);
        repl.runInteractions(System.out);
    }
}