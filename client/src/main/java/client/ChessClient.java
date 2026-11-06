package client;

import exceptions.*;
import model.AuthData;
import model.GameData;
import model.requests.JoinGameRequest;
import model.results.ListGamesResult;

import javax.xml.crypto.Data;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;
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
            case UnauthorizedException _ -> outStream.println("You are not authorized to " + command + ". Please login or register.");
            case BadRequestException _ -> outStream.println("You did not provide valid data to " + command);
            case AlreadyTakenException _ -> outStream.println("Your username is not unique. Please choose a different username and try again.");
            case DataAccessException _ -> outStream.println("There was a problem accessing the database. Please try again.");
            default -> outStream.println("Apologies, there was an error when executing " + command + ". Please try again.");
        }
    }

    public void clear(PrintStream outStream, Scanner scanner) {
        outStream.print("Are you sure you want to clear all of the databases? This will log you out and remove all users and games. (y/n) ");
        String y_n = scanner.nextLine().toLowerCase();
        if (y_n.equals("y")) {
            try {
                facade.clear();
                state = UserState.LOGGED_OUT;
                outStream.println("The database has been cleared!");
            } catch (Exception e) {
                handleErrors(e, outStream, "clear");
            }
        }
        else {
            outStream.println("Returning to normal processes :)");
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
            outStream.println("  list - view a list of all chess games");
            outStream.println("  create game - create a new chess game");
            outStream.println("  play game - create a new chess game");
            outStream.println("  observe game - create a new chess game");
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
        } catch(UnauthorizedException e) {
            outStream.println("Username or password are not correct. Please try again or register to create an account.");
        } catch(Exception e) { handleErrors(e, outStream, "login"); }
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
            auth = null;
            state = UserState.LOGGED_OUT;
            outStream.println("You are signed out!");
        } catch(Exception e) { handleErrors(e, outStream, "logout"); }
    }

    public void listGames(PrintStream outStream) {
        try {
            ListGamesResult result = facade.listGames(auth.authToken());
            ArrayList<GameData> games = result.games();
            if (games.isEmpty()) {
                outStream.println("There are no chess games yet. Type create game to make one.");
            }
            else {
                for (GameData game : games) {
                    outStream.println(game.toString());
                }
            }
        } catch(Exception e) { handleErrors(e, outStream, "list games"); }
    }

    public void createGame(PrintStream outStream, Scanner scanner) {
        outStream.print("Give your game a snazzy title: ");
        String name = scanner.nextLine();
        try {
            int gameID = facade.createGame(name, auth.authToken()).gameID();
            outStream.println("Created 1 new game:");
            outStream.println(gameID + ". " + name);
        } catch(Exception e) { handleErrors(e, outStream, "create game"); }
    }

    public void printGame(PrintStream outStream) {
        outStream.println("\nThis will print the board out!\n");
    }

    public void playGame(PrintStream outStream, Scanner scanner) {
        outStream.print("Game id: ");
        String id = scanner.nextLine();
        if ((id==null)||id.isEmpty()) {return;}
        while (!id.matches("\\d+")) {
            outStream.println("Please input a valid game id.");
            outStream.print("Game id: ");
            id = scanner.nextLine();
            if (id.equals("quit")) {return;}
        }
        outStream.print("Which color would you like to be? ");
        String color = scanner.nextLine().toLowerCase();
        if ((color.isEmpty())) {return;}
        while (!color.equals("w")&&!color.equals("white")&&!color.equals("b")&&!color.equals("black")) {
            outStream.println(color + " is not a valid color.");
            outStream.print("Which color would you like to be? ");
            color = scanner.nextLine().toLowerCase();
        }
        if (color.equals("w")||color.equals("white")) { color = "WHITE"; }
        else { color = "BLACK"; }
        try {
            facade.joinGame(Integer.valueOf(id), color, auth.authToken());
            outStream.println("You have joined game " + id + " as " + color.toLowerCase() + "!");
        } catch(Exception e) {
            handleErrors(e, outStream, "create game");
        }
    }

    public void observeGame(PrintStream outStream, Scanner scanner) {
        outStream.println("You can't observe a game yet.");
    }
}