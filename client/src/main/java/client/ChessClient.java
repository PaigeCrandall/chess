package client;

import model.AuthData;

import java.io.PrintStream;
import java.util.Scanner;

public class ChessClient {
    public ServerFacade facade;
    public ChessClient.UserState state;
    public AuthData auth;

    ChessClient(String serverUrl) {
        this.facade = new ServerFacade(serverUrl);
        this.state = ChessClient.UserState.LOGGED_OUT;
        this.auth = null;
    }

    public enum UserState {
        QUIT,
        LOGGED_OUT,
        LOGGED_IN
    }

    public UserState getState() {
        return state;
    }

    public void clear(PrintStream outStream) {
        try {
            facade.clear();
            outStream.println("The database has been cleared!");
        } catch(Exception e) {
            outStream.println("there was a problem clearing the database: " + e);
        }
    }

    public void help(PrintStream outStream) {
        if (state == UserState.LOGGED_OUT) {
            outStream.println("You are currently signed out. These are the commands you can run:");
            outStream.println("  login - sign in with a username and password");
            outStream.println("  register - create a new account");
        }
        else if (state == UserState.LOGGED_IN) {
            outStream.println("You are currently signed in! These are the commands you can run:");
            outStream.println("  logout - sign out");
        }
        outStream.println("  help - see a list of possible commands that you can run");
        outStream.println("  quit - exit the chess program");
    }

    public void quit(PrintStream outStream) {
        outStream.println("Bye!"); state = UserState.QUIT;
    }

    public void login(PrintStream outStream, Scanner scanner) {
        outStream.print("username: ");
        String username = scanner.nextLine();
        outStream.print("password: ");
        String password = scanner.nextLine();
        try {
            auth = facade.login(username, password);
            state = UserState.LOGGED_IN;
            outStream.println("You are signed in!");
        } catch(Exception e) {
            outStream.println("there was a problem registering the user: " + e);
        }
    }

    public void register(PrintStream outStream, Scanner scanner) {
        outStream.print("username: ");
        String username = scanner.nextLine();
        outStream.print("email: ");
        String email = scanner.nextLine();
        outStream.print("password: ");
        String password = scanner.nextLine();
        try {
            auth = facade.register(username, email, password);
            state = UserState.LOGGED_IN;
            outStream.println("You are signed in!");
        }
        catch(Exception e) {
            // write a function to handle these :)
            outStream.println("there was a problem registering the user: " + e);
        }
    }

    public void logout(PrintStream outStream) {
        try {
            facade.logout(auth.authToken());
            state = UserState.LOGGED_OUT;
            auth = null;
            outStream.println("You are signed out!");
        }
        catch(Exception e) {
            // write a function to handle these :)
            outStream.println("Can't log out! " + e);
        }
    }


}
