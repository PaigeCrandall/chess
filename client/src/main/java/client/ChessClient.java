package client;

import java.io.PrintStream;
import java.util.Scanner;

public class ChessClient {

    public void help(PrintStream outStream, Repl.UserState state) {
        if (state == Repl.UserState.LOGGED_OUT) {
            outStream.println("You are currently signed out. These are the commands you can run:");
            outStream.println("  quit - exit the chess program");
            outStream.println("  login - sign in with a username and password to create or join a game");
            outStream.println("  help - see a list of possible commands that you can run");
        }
    }

    public void login(PrintStream outStream, Scanner scanner) {
        outStream.print("username: ");
        String username = scanner.nextLine();
        outStream.print("password: ");
        String password = scanner.nextLine();
        outStream.println("now we'll log you in");
    }


}
