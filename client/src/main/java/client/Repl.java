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
            case "clear": client.clear(outStream, scanner); break;
            case "login": client.login(outStream, scanner); break;
            case "register": client.register(outStream, scanner); break;
            case "help": client.help(outStream); break;
            case "quit": client.quit(outStream); break;
            default: outStream.println(input + " is not a valid command."); client.help(outStream); break;
        }
    }

    public void handleLoggedInState(String input, PrintStream outStream) {
        switch (input) {
            case "clear": client.clear(outStream, scanner); break;
            case "logout": client.logout(outStream); break;
            case "list": client.listGames(outStream); break;
            case "create game": client.createGame(outStream, scanner); break;
            case "play game": client.playGame(outStream, scanner); break;
            case "observe game": client.observeGame(outStream, scanner); break;
            case "help": client.help(outStream); break;
            case "quit": client.quit(outStream); break;
            default: outStream.println(input + " is not a valid command."); client.help(outStream); break;
        }
    }

    public void runInteractions(PrintStream outStream) {
        System.out.println("Welcome to Chess ;)");
        while (!client.getState().equals(ChessClient.UserState.QUIT)) {
            outStream.print(">> ");
            String input = scanner.nextLine().toLowerCase();
            if (client.getState().equals(ChessClient.UserState.LOGGED_OUT)) {
                handleLoggedOutState(input, outStream);
            } else {
                handleLoggedInState(input, outStream);
            }
        }
    }

}
