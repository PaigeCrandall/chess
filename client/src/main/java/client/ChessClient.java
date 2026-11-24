package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import exceptions.*;
import model.AuthData;
import model.GameData;
import model.results.ListGamesResult;
import websocket.messages.ServerMessage;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageObserver {
    public ServerFacade facade;
    public ChessClient.UserState state;
    public AuthData auth;
    public String username;
    public ArrayList<GameData> listedGames;

    public GameData currentGame;
    public ChessGame.TeamColor color;
    PrintStream outStream;

    ChessClient(String serverUrl, PrintStream printLocation) {
        this.facade = new ServerFacade(serverUrl);
        this.state = ChessClient.UserState.LOGGED_OUT;
        this.auth = null;
        this.listedGames = new ArrayList<>();
        this.outStream = printLocation;
        this.currentGame = null;
        this.color=null;
    }

    public enum UserState {
        QUIT,
        LOGGED_OUT,
        LOGGED_IN,
        GAMEPLAY
    }

    public UserState getState() {
        return state;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }

    public void handleErrors(Exception e, String command) {
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

    public void clear() {
        Scanner scanner = new Scanner(System.in);
        outStream.print(SET_TEXT_FAINT + SET_TEXT_COLOR_WHITE +
                "Are you sure you want to clear all of the databases? " + SET_TEXT_BOLD +
                "This will log you out and remove all users and games. " + SET_TEXT_FAINT +
                SET_TEXT_COLOR_RED + "(y/n) ");
        resetColors();
        String y_n = scanner.nextLine().toLowerCase();
        if (y_n.equals("y")) {
            try {
                facade.clear();
                state = UserState.LOGGED_OUT;
                outStream.println("The database has been cleared!");
            } catch (Exception e) {
                handleErrors(e, "clear");
            }
        }
        else {
            outStream.println("Returning to normal processes :)");
        }
    }

    public void resetColors() {
        outStream.print(RESET_BG_COLOR + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT + RESET_TEXT_ITALIC);
    }

    public String getIcon() {
        if (color.equals(ChessGame.TeamColor.BLACK)) {
            return (SET_TEXT_COLOR_RED + BLACK_KING);
        }
        else if (color.equals(ChessGame.TeamColor.WHITE)) {
            return (SET_TEXT_COLOR_BLUE + WHITE_KING);
        }
        return (SET_TEXT_COLOR_WHITE + BLACK_KNIGHT);
    }

    public void loggedOutHelp() {
        outStream.println(SET_TEXT_BOLD + SET_TEXT_COLOR_LIGHT_GREY +
                "You are currently signed out. These are the commands you can run:" + SET_TEXT_FAINT);
        outStream.println(SET_TEXT_COLOR_BLUE + "  login" +
                SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - sign in with a username and password");
        outStream.println(SET_TEXT_COLOR_BLUE + "  register" +
                SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - create a new account");
        outStream.println(SET_TEXT_COLOR_BLUE + "  quit" + SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC +" - exit the chess program");
        resetColors();
    }

    public void loggedInHelp() {
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
        outStream.println(SET_TEXT_COLOR_BLUE + "  quit" + SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC +" - exit the chess program");
        resetColors();
    }

    public void gamePlayHelp() {
        outStream.println(SET_TEXT_BOLD + SET_TEXT_COLOR_LIGHT_GREY +
                "You are playing a game. These are the commands you can run:" + SET_TEXT_FAINT);
        outStream.println(SET_TEXT_COLOR_BLUE + "  redraw" +
                SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - refresh the board");
        outStream.println(SET_TEXT_COLOR_BLUE + "  highlight {chess square}" +
                SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - show all possible moves that a piece could make (example: highlight e2)");
        outStream.println(SET_TEXT_COLOR_BLUE + "  move {move}" +
                SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - move a chess piece (example: move e2e4)");
        outStream.println(SET_TEXT_COLOR_BLUE + "  leave" +
                SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - exit the game");
        outStream.println(SET_TEXT_COLOR_BLUE + "  resign" +
                SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC + " - submit to the enemy player and admit defeat");
        resetColors();
    }

    public void help() {
        if (state == UserState.LOGGED_OUT) {
            loggedOutHelp();
        }
        else if (state == UserState.LOGGED_IN) {
            loggedInHelp();
        }
        else if (state == UserState.GAMEPLAY) {
            gamePlayHelp();
        }
        outStream.println(SET_TEXT_COLOR_BLUE + "  help" + SET_TEXT_COLOR_WHITE + SET_TEXT_ITALIC +
                " - see a list of possible commands that you can run");
    }

    public void quit() {
        outStream.println(SET_TEXT_COLOR_WHITE + "Bye!"); state = UserState.QUIT;
    }

    public void login() {
        Scanner scanner = new Scanner(System.in);
        outStream.print(SET_TEXT_COLOR_BLUE + "username: ");
        String username = scanner.nextLine();
        outStream.print(SET_TEXT_COLOR_BLUE + "password: ");
        String password = scanner.nextLine();
        try {
            auth = facade.login(username, password);
            this.username = username;
            state = UserState.LOGGED_IN;
            outStream.println(SET_TEXT_COLOR_WHITE + "You are signed in!");
        } catch(UnauthorizedException e) {
            outStream.println(SET_TEXT_COLOR_RED + "Username or password are not correct. " +
                    "Please try again or register to create an account." + RESET_TEXT_COLOR);
        } catch(Exception e) { handleErrors(e, "login"); }
    }

    public void register() {
        Scanner scanner = new Scanner(System.in);
        outStream.print(SET_TEXT_COLOR_BLUE + "username: ");
        String username = scanner.nextLine();
        outStream.print(SET_TEXT_COLOR_BLUE + "email: ");
        String email = scanner.nextLine();
        outStream.print(SET_TEXT_COLOR_BLUE + "password: ");
        String password = scanner.nextLine();
        try {
            auth = facade.register(username, email, password);
            this.username = username;
            state = UserState.LOGGED_IN;
            outStream.println(SET_TEXT_COLOR_WHITE + "You are signed in!");
        } catch(AlreadyTakenException e) {
            outStream.println(SET_TEXT_COLOR_WHITE + "Your username is not unique. Please choose a different username.");
        } catch(Exception e) { handleErrors(e, "register"); }
    }

    public void logout() {
        try {
            facade.logout(auth.authToken());
            auth = null;
            username = null;
            state = UserState.LOGGED_OUT;
            outStream.println(SET_TEXT_COLOR_WHITE + "You are signed out!");
        } catch(Exception e) { handleErrors(e, "logout"); }
    }

    public void listGames() {
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
                    outStream.println(SET_TEXT_COLOR_WHITE + userFriendlyId + ". " +
                            SET_TEXT_COLOR_BLUE + game.gameName() + SET_TEXT_COLOR_WHITE +
                            "   white player: " + SET_TEXT_COLOR_MAGENTA + (game.whiteUsername()!=null?game.whiteUsername():"none")
                            + SET_TEXT_COLOR_WHITE +
                            "   black player: " + SET_TEXT_COLOR_MAGENTA + (game.blackUsername()!=null?game.blackUsername():"none")
                    );
                    resetColors();

                    userFriendlyId++;
                }
            }
        } catch(Exception e) { handleErrors(e, "list games"); }
    }

    public void createGame() {
        Scanner scanner = new Scanner(System.in);
        outStream.print(SET_TEXT_ITALIC + SET_TEXT_COLOR_BLUE + "Give your game a snazzy title: " + RESET_TEXT_ITALIC);
        String name = scanner.nextLine();
        try {
            facade.createGame(name, auth.authToken());
            outStream.println(SET_TEXT_COLOR_WHITE + "Created 1 new game.");
        } catch(Exception e) { handleErrors(e, "create game"); }
    }

    public void printChessBoard() {
        if (currentGame.winner()!=null) {
            outStream.print(SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD +
                    "This game is over! The winner was "
                    + SET_TEXT_COLOR_RED + currentGame.winner());
        }
        resetColors();
        ChessBoardRender renderer = new ChessBoardRender(outStream, currentGame.game().getBoard(), color.name(), "Chess Board");
        renderer.renderBoard();
    }

    public void printChessBoardHighlighted(String strPos) {
        resetColors();
        ChessPosition position = parseTwoLetterStringCommands(strPos);
        if (currentGame.game().getBoard().getPiece(position)==null) {
            outStream.println(SET_TEXT_COLOR_LIGHT_GREY + "There is no piece to move at " + strPos);
        }
        else {
            ChessBoardRender renderer = new ChessBoardRender(outStream, currentGame.game().getBoard(), color.name(), "Chess Board");
            Collection<ChessMove> movesToHighlight = currentGame.game().validMoves(position);
            Collection<ChessPosition> squaresToHighlight = new ArrayList<>();
            squaresToHighlight.add(position);
            for (ChessMove move : movesToHighlight) {
                squaresToHighlight.add(move.getEndPosition());
            }
            squaresToHighlight.add(position);
            renderer.renderBoardWithHighlight(squaresToHighlight);
        }
    }

    public ChessPosition parseTwoLetterStringCommands(String positions) {
        int row = Character.getNumericValue(positions.charAt(1));
        int column = 9-(positions.charAt(0) - 'a' + 1);
        return new ChessPosition(row, column);
    }

    public Integer getGameIdFromPlayer() {
        Scanner scanner = new Scanner(System.in);
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

    public String getColorFromPlayer() {
        Scanner scanner = new Scanner(System.in);
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

    public void playGame() {
        if (listedGames.isEmpty()) {
            outStream.println(SET_TEXT_COLOR_LIGHT_GREY + "Type list games first to see the available games");
        }
        else {
            Integer id = getGameIdFromPlayer();
            if (id==null) {return;}
            String color = getColorFromPlayer();
            if (color==null) {return;}
            try {

                facade.joinGame(id, color, auth.authToken());
                this.color = (color.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);
                outStream.println(SET_TEXT_COLOR_LIGHT_GREY + "You have joined game " + id + " as " + color.toLowerCase() + "!");

                facade.wsConnect(id, this, auth.authToken());
                state = UserState.GAMEPLAY;

            } catch(AlreadyTakenException e) {
                outStream.println(SET_TEXT_COLOR_RED + color.toLowerCase() + " is already taken.");
            } catch(DataAccessException e) {
                outStream.println(SET_TEXT_COLOR_RED + "The game does not exist.");
            } catch(Exception e) {
                handleErrors(e, "play game");
            }
        }
        resetColors();
    }

    public void observeGame() {
        if (listedGames.isEmpty()) {
            outStream.println(SET_TEXT_COLOR_LIGHT_GREY + "Type list games first to see the available games");
        }
        else {
            try {
                Integer id = getGameIdFromPlayer();
                if (id == null) { return; }
                this.color = ChessGame.TeamColor.WHITE;
                facade.wsConnect(id, this, auth.authToken());
                state = UserState.GAMEPLAY;
                outStream.println(SET_TEXT_COLOR_LIGHT_GREY + "You have joined game " + id + " as an observer.");

            } catch(DataAccessException e) {
                outStream.println(SET_TEXT_COLOR_RED + "The game does not exist.");
            } catch(Exception e) {
                handleErrors(e, "play game");
            }
        }
        resetColors();
    }

    public void markGameDone(String winner) {
        currentGame = new GameData(
                currentGame.gameID(),
                currentGame.whiteUsername(),
                currentGame.blackUsername(),
                currentGame.gameName(),
                currentGame.game()
        );
    }

    public void resign() {
        Scanner scanner = new Scanner(System.in);
        if (currentGame.whiteUsername()==null ||
                (color==ChessGame.TeamColor.WHITE&&!currentGame.whiteUsername().equals(username))) {
            outStream.println(SET_TEXT_COLOR_LIGHT_GREY + "Observers cannot resign. Type leave to exit the game.");
            return;
        }
        if (currentGame.winner()!=null) {
            outStream.println(SET_TEXT_COLOR_LIGHT_GREY + "This game is already over.");
            return;
        }
        outStream.print(SET_TEXT_FAINT + SET_TEXT_COLOR_WHITE +
                "Are you sure you want to resign? " + SET_TEXT_FAINT +
                SET_TEXT_COLOR_RED + "(y/n) ");
        resetColors();
        String y_n = scanner.nextLine().toLowerCase();
        if (y_n.equals("y")) {
            String winner = currentGame.blackUsername();
            if (color== ChessGame.TeamColor.BLACK) {
                winner = currentGame.whiteUsername();
            }
            markGameDone(winner);
        }
    }

    public void printUserPromptBasedOnStateAndColor() {
        if (state.equals(ChessClient.UserState.GAMEPLAY)) {
            outStream.print(getIcon() + SET_TEXT_COLOR_WHITE + " >> ");
        }
        else {
            outStream.print(SET_TEXT_COLOR_BLUE + ">> ");
        }
    }

    public void notify(ServerMessage message) {
        synchronized (System.out) {
            outStream.print("\n");
            if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                outStream.println(SET_TEXT_COLOR_BLUE + SET_TEXT_ITALIC);
                outStream.println(message.getServerMessage());
            } else if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                outStream.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC);
                outStream.println(message.getServerMessage());
            } else if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                currentGame = message.getGame();
                printChessBoard();
            }
            else {
                outStream.println(SET_TEXT_COLOR_LIGHT_GREY + "Received something else ????????????????");
            }
            resetColors();
            outStream.flush();
            printUserPromptBasedOnStateAndColor();
        }
    }
}