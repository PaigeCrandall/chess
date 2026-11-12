package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import java.util.List;
import java.util.Map;

import static ui.EscapeSequences.*;

import java.io.PrintStream;

public class ChessBoardRender {
    ChessBoard board;
    String color;
    PrintStream outstream;
    String gameName;


    public ChessBoardRender(PrintStream printOutStream, ChessBoard gameBoard, String persp, String name) {
        this.outstream = printOutStream;
        this.board = gameBoard;
        this.color = persp;
        this.gameName = name;

    }

    public static final Map<ChessPiece.PieceType, String> WHITE_PIECE_SHAPES = Map.of(
            ChessPiece.PieceType.PAWN, WHITE_PAWN,
            ChessPiece.PieceType.ROOK, WHITE_ROOK,
            ChessPiece.PieceType.KNIGHT, WHITE_KNIGHT,
            ChessPiece.PieceType.BISHOP, WHITE_BISHOP,
            ChessPiece.PieceType.QUEEN, WHITE_QUEEN,
            ChessPiece.PieceType.KING, WHITE_KING
    );

    public static final Map<ChessPiece.PieceType, String> BLACK_PIECE_SHAPES = Map.of(
            ChessPiece.PieceType.PAWN, BLACK_PAWN,
            ChessPiece.PieceType.ROOK, BLACK_ROOK,
            ChessPiece.PieceType.KNIGHT, BLACK_KNIGHT,
            ChessPiece.PieceType.BISHOP,  BLACK_BISHOP,
            ChessPiece.PieceType.QUEEN,  BLACK_QUEEN,
            ChessPiece.PieceType.KING,  BLACK_KING
    );

    public void renderBoard() {
        printHeading();
        if (color.equals("WHITE")) {
            printBoardFromWhitePerspective();
        }
        else {
            printBoardFromBlackPerspective();
        }
        outstream.print("\n");
    }

    public void printHeading() {
        outstream.print("\n");
        outstream.print(SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE);
        outstream.print(gameName.toUpperCase());
        outstream.print(RESET_TEXT_BOLD_FAINT + "\n");
    }

    public void resetColors() {
        outstream.print(RESET_BG_COLOR + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);
    }

    public void printEmptySpace(String bgColor) {
        outstream.print(bgColor + EMPTY);
        resetColors();
    }

    public void printPiece(ChessPiece piece, String bgColor) {
        String pieceColor = getPieceColor(piece);
        String symbol = EMPTY;
        if (piece.getTeamColor()==ChessGame.TeamColor.WHITE) {
            symbol = WHITE_PIECE_SHAPES.get(piece.getPieceType());
        }
        else if (piece.getTeamColor()==ChessGame.TeamColor.BLACK) {
            symbol = BLACK_PIECE_SHAPES.get(piece.getPieceType());
        }
        outstream.print(bgColor + pieceColor + symbol);
        resetColors();
    }

    public String getSquareColor(ChessPosition position) {
        if (position.getRow() % 2 != 0) {
            if (position.getColumn()%2 != 0) {
                return SET_BG_COLOR_WHITE;
            }
            else {
                return SET_BG_COLOR_LIGHT_GREY;
            }
        }
        else {
            if (position.getColumn()%2 != 0) {
                return SET_BG_COLOR_LIGHT_GREY;
            }
            else {
                return SET_BG_COLOR_WHITE;
            }
        }
    }

    public String getPieceColor(ChessPiece piece) {
        if (piece.getTeamColor()== ChessGame.TeamColor.WHITE) {
            return SET_TEXT_COLOR_BLUE;
        }
        else {
            return SET_TEXT_COLOR_RED;
        }
    }

    public void printSquare(ChessPosition position) {
        String squareColor = getSquareColor(position);
        if(board.getPiece(position)==null) {
            printEmptySpace(squareColor);
        }
        else {
            printPiece(board.getPiece(position), squareColor);
        }
    }

    public void printColorRow(String colorRow) {
        printEmptySpace(colorRow);
        List<String> list = List.of("a", "b", "c", "d", "e", "f", "g", "h");
        for (String i : list) {
            outstream.print(colorRow + SET_TEXT_COLOR_DARK_GREY + SET_TEXT_BOLD + " " + i + " ");
            resetColors();
        }
        printEmptySpace(colorRow);
        outstream.print("\n");
    }

    public void printBoardFromBlackPerspective() {
        printColorRow(SET_BG_COLOR_RED);
        for (int y = 1; y<9; y++) {
            outstream.print(SET_BG_COLOR_RED + SET_TEXT_COLOR_DARK_GREY  + SET_TEXT_BOLD + " " + y + " ");
            for (int x=1; x<9; x++) {
                printSquare(new ChessPosition(y, x));
            }
            outstream.print(SET_BG_COLOR_RED + SET_TEXT_COLOR_DARK_GREY  + SET_TEXT_BOLD + " " + y + " ");
            resetColors();
            outstream.print("\n");
        }
        printColorRow(SET_BG_COLOR_RED);
    }

    public void printBoardFromWhitePerspective() {
        printColorRow(SET_BG_COLOR_BLUE);
        for (int y = 8; y>0; y--) {
            outstream.print(SET_BG_COLOR_BLUE + SET_TEXT_COLOR_DARK_GREY  + SET_TEXT_BOLD + " " + y + " ");
            for (int x=8; x>0; x--) {
                printSquare(new ChessPosition(y, x));
            }
            outstream.print(SET_BG_COLOR_BLUE + SET_TEXT_COLOR_DARK_GREY  + SET_TEXT_BOLD + " " + y + " ");
            resetColors();
            outstream.print("\n");
        }
        printColorRow(SET_BG_COLOR_BLUE);
    }


}
