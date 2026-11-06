package client;

import exceptions.*;
import model.AuthData;

import javax.xml.crypto.Data;
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

    public void handleErrors(Exception e, PrintStream outStream, String command) {
        switch (e) {
            case UnauthorizedException _ -> outStream.println("You are not authorized to " + command);
            case BadRequestException _ -> outStream.println("You did not provide valid data to " + command);
            case AlreadyTakenException _ -> outStream.println("Your username is not unique. Please choose a different username and try again.");
            case DataAccessException _ -> outStream.println("There was a problem accessing the database. Please try again.");
            default -> outStream.println("Apologies, there was an error when executing " + command + ". Please try again.");
        }
    }

    public void clear(PrintStream outStream) {
        try {
            facade.clear();
            outStream.println("The database has been cleared!");
        } catch(Exception e) { handleErrors(e, outStream, "clear"); }
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
        }  catch(Exception e) { handleErrors(e, outStream, "login"); }
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
        } catch(Exception e) { handleErrors(e, outStream, "register"); }
    }

    public void logout(PrintStream outStream) {
        try {
            facade.logout(auth.authToken());
            state = UserState.LOGGED_OUT;
            auth = null;
            outStream.println("You are signed out!");
        } catch(Exception e) { handleErrors(e, outStream, "logout"); }
    }

}