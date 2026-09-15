package chess;

// A chessboard that can hold and rearrange chess pieces.

public class ChessBoard {
    private ChessPiece[][] board;
    
    public ChessBoard() {
        board = new ChessPiece[9][9];
    }

    public void printBoard() {
        System.out.println("    1 2 3 4 5 6 7 8");
        for (int i = 8; i>=1; i--) {
            System.out.printf("%s  ", i);
            for (int n = 1; n<=8; n++) {
                System.out.print("|");
                if (board[i][n] == null) {
                    System.out.print(" ");
                }
                else {
                    System.out.printf(board[i][n].getLetter());
                }
            }
            System.out.print("|\n");
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
        setPiece(1, 5, ChessPiece.PieceType.KING, ChessGame.TeamColor.BLACK);
        setPiece(8, 5, ChessPiece.PieceType.KING, ChessGame.TeamColor.WHITE);

    }

    // For Testing
//    public static void main(String[] args) {
//        ChessBoard gameboard = new ChessBoard();
//        gameboard.setPiece(2, 3, ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.WHITE);
//        gameboard.printBoard();
//        ChessPosition position = new ChessPosition(2,3);
//        ChessPiece piece = gameboard.getPiece(position);
//        piece.printMoves(gameboard, position);
//    }
}
