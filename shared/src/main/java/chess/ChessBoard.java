package chess;

// A chessboard that can hold and rearrange chess pieces.

public class ChessBoard {
    private ChessPiece[][] board;
    
    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    public void printBoard() {
        for (int i = 0; i<8; i++) {
            System.out.printf("%s  ", i+1);
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
    public void resetBoard() {
        board = new ChessPiece[8][8];

        // add pawns
        for (int i=0; i<8; i++) {
            ChessPosition whitePosition = new ChessPosition(0, i);
            ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            addPiece(whitePosition, whitePawn);

            ChessPosition blackPosition = new ChessPosition(7, i);
            ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            addPiece(blackPosition, blackPawn);
        }
    }

//    public static void main(String[] args) {
//        ChessBoard gameboard = new ChessBoard();
//        gameboard.resetBoard();
//        gameboard.printBoard();
//    }
//}
