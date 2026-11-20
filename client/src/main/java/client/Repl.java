package client;

import chess.ChessBoard;

import java.io.PrintStream;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    public ChessClient client;
    public Scanner scanner;

    public Repl(String serverUrl) {
        this.client = new ChessClient(serverUrl);
        this.scanner = new Scanner(System.in);
    }

    public boolean checkForBadGameplayInput(String[] words, PrintStream outStream) {
        if ((words.length == 2) && (
                (words[0].equals("move") && !words[1].matches("[a-g][1-8][a-g][1-8]")) ||
                        (words[0].equals("highlight") && !words[1].matches("[a-g][1-8]")) ||
                        !(words[0].equals("move")) && !(words[0].equals("highlight"))
        ) || (words.length > 2)
                || ((words[0].equals("move")||words[0].equals("highlight")) && (words.length < 2))
        ) {
            outStream.println(SET_TEXT_COLOR_RED + String.join(" ", words) + " is not a valid command."); client.help(outStream);
            return true;
        }
        return false;
    }

    public void handleGameplayState(String input, PrintStream outStream) {
        String[] words = input.trim().split("\\s+");
        if (checkForBadGameplayInput( words, outStream)) {return;}
        String command = words[0];
        switch (command) {
            case "redraw": outStream.println("Imagine a beautiful new chessboard is printed before your eyes."); break;
            case "highlight": outStream.println("Show moves for: " + words[1]); break;
            case "move": outStream.println("make move: " + words[1]); break;
            case "leave": outStream.println("You are trying to leave the game."); break;
            case "resign": outStream.println("You have now lost the game."); break;
            case "help": client.help(outStream); break;
            default: outStream.println(SET_TEXT_COLOR_RED + input + " is not a valid command."); client.help(outStream); break;
        }
    }

    public void handleLoggedOutState(String input, PrintStream outStream) {
        switch (input) {
            case "clear": client.clear(outStream, scanner); break;
            case "login": client.login(outStream, scanner); break;
            case "register": client.register(outStream, scanner); break;
            case "help": client.help(outStream); break;
            case "quit": client.quit(outStream); break;
            default: outStream.println(SET_TEXT_COLOR_RED + input + " is not a valid command."); client.help(outStream); break;
        }
    }

    public void handleLoggedInState(String input, PrintStream outStream) {
        switch (input) {
            case "clear": client.clear(outStream, scanner); break;
            case "logout": client.logout(outStream); break;
            case "list games": client.listGames(outStream); break;
            case "create game": client.createGame(outStream, scanner); break;
            case "play game": client.playGame(outStream, scanner); break;
            case "observe game": client.observeGame(outStream, scanner); break;
            case "help": client.help(outStream); break;
            case "quit": client.quit(outStream); break;
            default: outStream.println(SET_TEXT_COLOR_RED + input + " is not a valid command."); client.help(outStream); break;
        }
    }

    public void runInteractions(PrintStream outStream) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardRender renderer = new ChessBoardRender(System.out, board, "BLACK", "Chess Board");
        renderer.printBoardFromBlackPerspective();
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "   " + WHITE_KING +
                SET_TEXT_COLOR_WHITE + "Welcome to Chess!"
                + SET_TEXT_COLOR_BLUE + WHITE_KING
                + RESET_TEXT_BOLD_FAINT);

        while (!client.getState().equals(ChessClient.UserState.QUIT)) {
            outStream.print(SET_TEXT_COLOR_BLUE + ">> ");
            String input = scanner.nextLine().toLowerCase();
            if (client.getState().equals(ChessClient.UserState.LOGGED_OUT)) {
                handleLoggedOutState(input, outStream);
            } else if (client.getState().equals(ChessClient.UserState.LOGGED_IN)) {
                handleLoggedInState(input, outStream);
            } else if (client.getState().equals(ChessClient.UserState.GAMEPLAY)) {
                handleGameplayState(input, outStream);
            }
        }
    }

}
