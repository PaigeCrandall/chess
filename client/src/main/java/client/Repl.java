package client;

import chess.ChessBoard;
import chess.ChessGame;

import java.io.PrintStream;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    public ChessClient client;
    public Scanner scanner;
    PrintStream outStream;

    public Repl(String serverUrl) {
        this.outStream = System.out;
        this.client = new ChessClient(serverUrl, outStream);
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
            outStream.println(SET_TEXT_COLOR_RED + String.join(" ", words) + " is not a valid command."); client.help();
            return true;
        }
        return false;
    }

    public void handleGameplayState(String input) {
        String[] words = input.trim().split("\\s+");
        if (checkForBadGameplayInput( words, outStream)) {return;}
        String command = words[0];
        switch (command) {
            case "redraw": outStream.println("Imagine a beautiful new chessboard is printed before your eyes."); break;
            case "highlight": outStream.println("Show moves for: " + words[1]); break;
            case "move": outStream.println("make move: " + words[1]); break;
            case "leave": outStream.println("You are trying to leave the game."); break;
            case "resign": outStream.println("You have now lost the game."); break;
            case "help": client.help(); break;
            default: outStream.println(SET_TEXT_COLOR_RED + input + " is not a valid command."); client.help(); break;
        }
    }

    public void handleLoggedOutState(String input) {
        switch (input) {
            case "clear": client.clear(); break;
            case "login": client.login(); break;
            case "register": client.register(); break;
            case "help": client.help(); break;
            case "quit": client.quit(); break;
            default: outStream.println(SET_TEXT_COLOR_RED + input + " is not a valid command."); client.help(); break;
        }
    }

    public void handleLoggedInState(String input) {
        switch (input) {
            case "clear": client.clear(); break;
            case "logout": client.logout(); break;
            case "list games": client.listGames(); break;
            case "create game": client.createGame(); break;
            case "play game": client.playGame(); break;
            case "observe game": client.observeGame(); break;
            case "help": client.help(); break;
            case "quit": client.quit(); break;
            default: outStream.println(SET_TEXT_COLOR_RED + input + " is not a valid command."); client.help(); break;
        }
    }

    public void runInteractions(PrintStream outStream) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardRender renderer = new ChessBoardRender(System.out, board, "BLACK", "Chess Board");
        renderer.printBoardFromBlackPerspective();
        outStream.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "   " + WHITE_KING +
                SET_TEXT_COLOR_WHITE + "Welcome to Chess!"
                + SET_TEXT_COLOR_BLUE + WHITE_KING
                + RESET_TEXT_BOLD_FAINT);

        while (!client.getState().equals(ChessClient.UserState.QUIT)) {
            if (client.getState().equals(ChessClient.UserState.GAMEPLAY)) {
                if (client.getColor()== ChessGame.TeamColor.WHITE) {
                    outStream.print(SET_TEXT_COLOR_BLUE);
                }
                else if (client.getColor()== ChessGame.TeamColor.BLACK) {
                    outStream.print(SET_TEXT_COLOR_RED);
                }
                outStream.println(WHITE_KING + SET_TEXT_COLOR_WHITE + " >> ");
            }
            else {
                outStream.print(SET_TEXT_COLOR_BLUE + ">> ");
            }
            String input = scanner.nextLine().toLowerCase();
            if (client.getState().equals(ChessClient.UserState.LOGGED_OUT)) {
                handleLoggedOutState(input);
            } else if (client.getState().equals(ChessClient.UserState.LOGGED_IN)) {
                handleLoggedInState(input);
            } else if (client.getState().equals(ChessClient.UserState.GAMEPLAY)) {
                handleGameplayState(input);
            }
        }
    }

}
