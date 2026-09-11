package chess;

// A chessboard that can hold and rearrange chess pieces.

public class ChessBoard {
    private ChessPiece[][] board;
    
    public ChessBoard() {
        resetBoard();
    }

    public void printBoard() {
        System.out.println("    a   b   c   d   e   f   g   h");
        for (int i = 1; i<=8; i++) {
            System.out.printf("%s  ", 9-i);
            for (int n = 0; n<8; n++) {
                System.out.print("|");
                if (board[i][n] == null) {
                    System.out.print(" ");
                }
                else {
                    System.out.printf(board[i][n].toString());
                }
                System.out.print("| ");
            }
            System.out.print("\n");
        }
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
            setPiece(1, i, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE);
            setPiece(6, i, ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK);
        }

        // add Rooks
        setPiece(0, 0, ChessPiece.PieceType.ROOK, ChessGame.TeamColor.BLACK);
        setPiece(0, 7, ChessPiece.PieceType.ROOK, ChessGame.TeamColor.BLACK);
        setPiece(7, 0, ChessPiece.PieceType.ROOK, ChessGame.TeamColor.WHITE);
        setPiece(7, 7, ChessPiece.PieceType.ROOK, ChessGame.TeamColor.WHITE);

        // add Knights
        setPiece(0, 1, ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.BLACK);
        setPiece(0, 6, ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.BLACK);
        setPiece(7, 1, ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.WHITE);
        setPiece(7, 6, ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.WHITE);

        // add Bishops
        setPiece(0, 2, ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.BLACK);
        setPiece(0, 5, ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.BLACK);
        setPiece(7, 2, ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.WHITE);
        setPiece(7, 5, ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.WHITE);

        // add Queens
        setPiece(0, 3, ChessPiece.PieceType.QUEEN, ChessGame.TeamColor.BLACK);
        setPiece(7, 3, ChessPiece.PieceType.QUEEN, ChessGame.TeamColor.WHITE);

        // add Kings
        setPiece(0, 4, ChessPiece.PieceType.KING, ChessGame.TeamColor.BLACK);
        setPiece(7, 4, ChessPiece.PieceType.KING, ChessGame.TeamColor.WHITE);

    }

    // For Testing
    public static void main(String[] args) {
        ChessBoard gameboard = new ChessBoard();
        gameboard.printBoard();
    }
}
