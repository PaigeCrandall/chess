package chess;

// A chessboard that can hold and rearrange chess pieces.

import java.util.Arrays;
import java.util.Objects;

public class ChessBoard {
    private ChessPiece[][] board;
    
    public ChessBoard() {
        board = new ChessPiece[9][9];
    }

    // equals and hashcode
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }


    public ChessBoard copy() {
        ChessBoard clonedBoard = new ChessBoard();
        for (int row=1; row<=8; row++) {
            for (int col=1; col<=8; col++) {
                if (this.getPiece(new ChessPosition(row, col))!=null) {
                    clonedBoard.addPiece(new ChessPosition(row, col), this.getPiece(new ChessPosition(row, col)));
                }
            }
        }
        return clonedBoard;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
    */

    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()][position.getColumn()] = piece;
    }

    public void removePiece(ChessPosition position) {
        board[position.getRow()][position.getColumn()] = null;
    }


    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()][position.getColumn()];
    }

    public ChessPosition findPiecePosition(ChessPiece.PieceType type, ChessGame.TeamColor color) {
        for (int x=1; x<9; x++) {
            for (int y=1; y<9;y++) {
                if(getPiece(new ChessPosition(y,x))!=null) {
                    if ((getPiece(new ChessPosition(y,x)).getPieceType()==type)&&((getPiece(new ChessPosition(y,x)).getTeamColor()==color))) {
                        return (new ChessPosition(y,x));
                    }
                }
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void setPiece(int row, int column, ChessPiece.PieceType type, ChessGame.TeamColor color) {
        ChessPosition position = new ChessPosition(row, column);
        ChessPiece piece = new ChessPiece(color, type);
        addPiece(position, piece);
    }

    public void resetBoard() {
        board = new ChessPiece[9][9];

        // add pawns
        for (int i=1; i<=8; i++) {
            setPiece(2, i, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE);
            setPiece(7, i, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK);
        }

        // add Rooks
        setPiece(1, 1, ChessPiece.PieceType.ROOK, ChessGame.TeamColor.WHITE);
        setPiece(1, 8, ChessPiece.PieceType.ROOK, ChessGame.TeamColor.WHITE);
        setPiece(8, 1, ChessPiece.PieceType.ROOK, ChessGame.TeamColor.BLACK);
        setPiece(8, 8, ChessPiece.PieceType.ROOK, ChessGame.TeamColor.BLACK);

        // add Knights
        setPiece(1, 2, ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.WHITE);
        setPiece(1, 7, ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.WHITE);
        setPiece(8, 2, ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.BLACK);
        setPiece(8, 7, ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.BLACK);

        // add Bishops
        setPiece(1, 3, ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.WHITE);
        setPiece(1, 6, ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.WHITE);
        setPiece(8, 3, ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.BLACK);
        setPiece(8, 6, ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.BLACK);

        // add Queens
        setPiece(1, 4, ChessPiece.PieceType.QUEEN, ChessGame.TeamColor.WHITE);
        setPiece(8, 4, ChessPiece.PieceType.QUEEN, ChessGame.TeamColor.BLACK);

        // add Kings
        setPiece(1, 5, ChessPiece.PieceType.KING, ChessGame.TeamColor.WHITE);
        setPiece(8, 5, ChessPiece.PieceType.KING, ChessGame.TeamColor.BLACK);

    }

}
