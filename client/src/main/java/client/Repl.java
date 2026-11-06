package client;

import java.io.PrintStream;
import java.util.Scanner;

public class Repl {
    public ChessClient client;
    public Scanner scanner;

    public Repl(String serverUrl) {
        this.client = new ChessClient(serverUrl);
        this.scanner = new Scanner(System.in);
    }

    public void handleLoggedOutState(String input, PrintStream outStream) {
        switch (input) {
            case "clear": client.clear(outStream); break;
            case "login": client.login(outStream, scanner); break;
            case "register": client.register(outStream, scanner); break;
            case "help": client.help(outStream); break;
            case "quit": client.quit(outStream); break;
            default: outStream.println(input + " is not a valid command."); client.help(outStream); break;
        }
    }

    public void handleLoggedInState(String input, PrintStream outStream) {
        switch (input) {
            case "clear": client.clear(outStream); break;
            case "logout": client.logout(outStream); break;
            case "list games": client.listGames(outStream); break;
            case "help": client.help(outStream); break;
            case "quit": client.quit(outStream); break;
            default: outStream.println(input + " is not a valid command."); client.help(outStream); break;
        }
    }

    public void runInteractions(PrintStream outStream) {
        System.out.println("Welcome to Chess ;)");
        while (!client.getState().equals(ChessClient.UserState.QUIT)) {
            outStream.print(">> ");
            String input = scanner.nextLine();
            if (client.getState().equals(ChessClient.UserState.LOGGED_OUT)) {
                handleLoggedOutState(input, outStream);
            } else {
                handleLoggedInState(input, outStream);
            }
        }
    }

}
