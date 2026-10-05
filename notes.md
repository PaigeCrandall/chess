Chess Code for testing

In ChessGame

    // Main For Testing
    public static void main(String[] args) {
        ChessBoard gameboard = new ChessBoard();
        gameboard.addPiece((new ChessPosition(2,2)), (new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING)));
        gameboard.addPiece((new ChessPosition(4,4)), (new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK)));
        gameboard.addPiece((new ChessPosition(8,8)), (new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN)));
        gameboard.addPiece((new ChessPosition(8,1)), (new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING)));
    }

In ChessPiece

    public void printMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = pieceMoves(board, myPosition);
        for (ChessMove move : moves) {
            System.out.printf("%s, ", move.toString());
        }
    }


In ChessBoard

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