package client;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.function.Consumer;

public class Repl {
    public UserState state;
    public ChessClient client;

    public Repl() {
        this.state = UserState.LOGGED_OUT;
        this.client = new ChessClient();
    }

    public enum UserState {
        QUIT,
        LOGGED_OUT,
        LOGGED_IN
    }

    public void handleLoggedOutState(String input, PrintStream outStream) {
        Scanner scanner = new Scanner(System.in);
        switch (input) {
            case "help": client.help(outStream, state); break;
            case "login": client.login(outStream, scanner); break;
            case "quit": outStream.println("Bye!"); state=UserState.QUIT; break;
        }
    }

    public void runInteractions(PrintStream outStream) {
        System.out.println("Welcome to Chess ;)");
        while (!state.equals(UserState.QUIT)) {
            Scanner scanner = new Scanner(System.in);
            outStream.print(">> ");
            String input = scanner.nextLine();
            if (state == UserState.LOGGED_OUT) {
                handleLoggedOutState(input, outStream);
            } else {
                outStream.println("you are logged in");
                state = UserState.QUIT;
            }
        }
    }

}
