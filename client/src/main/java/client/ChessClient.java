package client;

import chess.ChessGame;
import exceptions.*;
import model.AuthData;
import model.GameData;
import model.results.ListGamesResult;
import java.io.PrintStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    public ServerFacade facade;
    public ChessClient.UserState state;
    public AuthData auth;
    public ArrayList<GameData> listedGames;

    ChessClient(String serverUrl) {
        this.facade = new ServerFacade(serverUrl);
        this.state = ChessClient.UserState.LOGGED_OUT;
        this.auth = null;
        this.listedGames = new ArrayList<>();
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
        outStream.print(SET_TEXT_COLOR_RED);
        switch (e) {
            case UnauthorizedException _ -> outStream.println("You are not authorized to " + command + ". Please login or register.");
            case BadRequestException _ -> outStream.println("You did not provide valid data to " + command);
            case AlreadyTakenException _ -> outStream.println("You tried to do something that was already taken.");
            case DataAccessException _ -> outStream.println("There was a problem accessing the database. Please try again.");
            case ConnectException ignored -> outStream.println("You are not connected to the server!");
            default -> outStream.println("Apologies, there was an error when executing " + command + ". Please try again. " + e);
        }
        outStream.print(RESET_TEXT_COLOR);
    }

    public void clear(PrintStream outStream, Scanner scanner) {
        outStream.print(SET_TEXT_FAINT + SET_TEXT_COLOR_WHITE +
                "Are you sure you want to clear all of the databases? " + SET_TEXT_BOLD +
                "This will log you out and remove all users and games. " + SET_TEXT_FAINT +
                SET_TEXT_COLOR_RED + "(y/n) ");
        resetColors(outStream);
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

    public void resetColors(PrintStream outStream) {
        outStream.print(RESET_BG_COLOR + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT + RESET_TEXT_ITALIC);
    }

    public void help(PrintStream outStream) {
        if (state == UserState.LOGGED_OUT) {
            outStream.println(SET_TEXT_BOLD + SET_TEXT_COLOR_LIGHT_GREY +
                    "You are currently signed out. These are the commands you can run:" + SET_TEXT_FAINT);
            outStream.println(SET_TEXT_COLOR_BLUE + "  login" +
                    SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - sign in with a username and password");
            outStream.println(SET_TEXT_COLOR_BLUE + "  register" + SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - create a new account");
        }
        else if (state == UserState.LOGGED_IN) {
            outStream.println(SET_TEXT_BOLD + SET_TEXT_COLOR_LIGHT_GREY + "You are currently signed in as " +
                    auth.username() + "! These are the commands you can run:" + SET_TEXT_FAINT);
            outStream.println(SET_TEXT_COLOR_BLUE + "  logout" + SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - sign out");
            outStream.println(SET_TEXT_COLOR_BLUE + "  list games" +
                    SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - view a list of all chess games");
            outStream.println(SET_TEXT_COLOR_BLUE + "  create game" +
                    SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - create a new chess game");
            outStream.println(SET_TEXT_COLOR_BLUE + "  play game" +
                    SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - create a new chess game");
            outStream.println(SET_TEXT_COLOR_BLUE + "  observe game" + SET_TEXT_COLOR_WHITE +
                    SET_TEXT_ITALIC + " - create a new chess game");
        }
        outStream.println(SET_TEXT_COLOR_BLUE + "  help" + SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - see a list of possible commands that you can run");
        outStream.println(SET_TEXT_COLOR_BLUE + "  quit" + SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - exit the chess program");
        resetColors(outStream);
    }

    public void quit(PrintStream outStream) {
        outStream.println("Bye!"); state = UserState.QUIT;
    }

    public void login(PrintStream outStream, Scanner scanner) {
        outStream.print(SET_TEXT_COLOR_BLUE + "username: ");
        String username = scanner.nextLine();
        outStream.print(SET_TEXT_COLOR_BLUE + "password: ");
        String password = scanner.nextLine();
        try {
            auth = facade.login(username, password);
            state = UserState.LOGGED_IN;
            outStream.println(SET_TEXT_COLOR_WHITE + "You are signed in!");
        } catch(UnauthorizedException e) {
            outStream.println(SET_TEXT_COLOR_RED + "Username or password are not correct. " +
                    "Please try again or register to create an account." + RESET_TEXT_COLOR);
        } catch(Exception e) { handleErrors(e, outStream, "login"); }
    }

    public void register(PrintStream outStream, Scanner scanner) {
        outStream.print(SET_TEXT_COLOR_BLUE + "username: ");
        String username = scanner.nextLine();
        outStream.print(SET_TEXT_COLOR_BLUE + "email: ");
        String email = scanner.nextLine();
        outStream.print(SET_TEXT_COLOR_BLUE + "password: ");
        String password = scanner.nextLine();
        try {
            auth = facade.register(username, email, password);
            state = UserState.LOGGED_IN;
            outStream.println(SET_TEXT_COLOR_WHITE + "You are signed in!");
        } catch(AlreadyTakenException e) {
            outStream.println(SET_TEXT_COLOR_WHITE + "Your username is not unique. Please choose a different username.");
        } catch(Exception e) { handleErrors(e, outStream, "register"); }
    }

    public void logout(PrintStream outStream) {
        try {
            facade.logout(auth.authToken());
            auth = null;
            state = UserState.LOGGED_OUT;
            outStream.println(SET_TEXT_COLOR_WHITE + "You are signed out!");
        } catch(Exception e) { handleErrors(e, outStream, "logout"); }
    }

    public void listGames(PrintStream outStream) {
        listedGames = new ArrayList<>();
        try {
            ListGamesResult result = facade.listGames(auth.authToken());
            ArrayList<GameData> games = result.games();
            if (games.isEmpty()) {
                outStream.println(SET_TEXT_COLOR_WHITE + "There are no chess games yet. Type create game to make one.");
            }
            else {
                outStream.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD +
                        "Existing Chess Games:" + RESET_TEXT_BOLD_FAINT);
                int userFriendlyId = 1;
                for (GameData game : games) {
                    listedGames.add(game);
                    outStream.println(SET_TEXT_COLOR_WHITE + userFriendlyId + ". " + SET_TEXT_COLOR_BLUE + game.gameName());
                    userFriendlyId++;
                }
            }
        } catch(Exception e) { handleErrors(e, outStream, "list games"); }
    }

    public void createGame(PrintStream outStream, Scanner scanner) {
        outStream.print(SET_TEXT_ITALIC + SET_TEXT_COLOR_BLUE + "Give your game a snazzy title: " + RESET_TEXT_ITALIC);
        String name = scanner.nextLine();
        try {
            int gameID = facade.createGame(name, auth.authToken()).gameID();
            outStream.println(SET_TEXT_COLOR_WHITE + "Created 1 new game:");
            outStream.println(SET_TEXT_COLOR_WHITE + gameID + ". " + SET_TEXT_COLOR_BLUE + name);
        } catch(Exception e) { handleErrors(e, outStream, "create game"); }
    }

    public void printChessBoard(PrintStream outStream, ChessGame game, String colorPerspective) {
        ChessBoardRender renderer = new ChessBoardRender(outStream, game.getBoard(), colorPerspective, "Chess Board");
        renderer.renderBoard();
    }

    public Integer getGameIdFromPlayer(PrintStream outStream, Scanner scanner) {
        outStream.print(SET_TEXT_COLOR_BLUE + "Game number: ");
        String userFriendlyId = scanner.nextLine();
        if (!userFriendlyId.matches("\\d+") ||
                (Integer.parseInt(userFriendlyId) < 1) ||
                (Integer.parseInt(userFriendlyId)>listedGames.size())) {
            outStream.println(SET_TEXT_COLOR_RED + "Please input a valid game number.");
            return null;
        }
        return listedGames.get(Integer.parseInt(userFriendlyId)-1).gameID();
    }

    public String getColorFromPlayer(PrintStream outStream, Scanner scanner) {
        outStream.print(SET_TEXT_COLOR_BLUE + "Which color would you like to be? ");
        String color = scanner.nextLine().toLowerCase();
        if (color.equals("w")||color.equals("white")) { color = "WHITE"; }
        else if (color.equals("b")||color.equals("black")) { color = "BLACK"; }
        else {
            outStream.println(SET_TEXT_COLOR_RED + color + " is not a valid color.");
            color = null;
        }
        return color;
    }

    public void playGame(PrintStream outStream, Scanner scanner) {
        if (listedGames.isEmpty()) {
            outStream.println(SET_TEXT_COLOR_LIGHT_GREY + "Type list games first to see the available games");
        }
        else {
            Integer id = getGameIdFromPlayer(outStream, scanner);
            if (id==null) {return;}
            String color = getColorFromPlayer(outStream, scanner);
            if (color==null) {return;}
            try {
                facade.joinGame(id, color, auth.authToken());
                outStream.println(SET_TEXT_COLOR_LIGHT_GREY + "You have joined game " + id + " as " + color.toLowerCase() + "!");
                outStream.println("You can't play a game yet, but here's a new board.");
                printChessBoard(outStream, new ChessGame(), color);
            } catch(AlreadyTakenException e) {
                outStream.println(SET_TEXT_COLOR_RED + color.toLowerCase() + " is already taken.");
            } catch(DataAccessException e) {
                outStream.println(SET_TEXT_COLOR_RED + "The game does not exist.");
            } catch(Exception e) {
                handleErrors(e, outStream, "create game");
            }
        }
        resetColors(outStream);
    }

    public void observeGame(PrintStream outStream, Scanner scanner) {
        if (listedGames.isEmpty()) {
            outStream.println(SET_TEXT_COLOR_LIGHT_GREY + "Type list games first to see the available games");
        }
        else {
            Integer id = getGameIdFromPlayer(outStream, scanner);
            if (id == null) {
                return;
            }
            outStream.println(SET_TEXT_COLOR_LIGHT_GREY + "You have joined game " + id + " as an observer.");
            outStream.println("You can't observe a game yet, but here's a new board.");
            printChessBoard(outStream, new ChessGame(), "WHITE");
        }
        resetColors(outStream);
    }
}