package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board;
    TeamColor team;

    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
        this.team = TeamColor.WHITE;
    }

    public ChessGame(ChessBoard board) {
        this.board = board;
        this.team = TeamColor.WHITE;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && team == chessGame.team;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, team);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition)!=null) {
            ChessPiece piece = board.getPiece(startPosition);
            Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

            // check for check :)
            Collection<ChessMove> movesListIterator = piece.pieceMoves(board, startPosition);
            for (ChessMove possibleMove : movesListIterator) {
                // for each move, imagine if the piece moved there
                ChessBoard imaginaryBoard = board.copy();
                ChessGame imaginaryGame = new ChessGame(imaginaryBoard);
                imaginaryGame.movePiece(possibleMove);
                // check if your king is in check in the new arrangement
                if (imaginaryGame.isInCheck(team)) {
//                    System.out.println("can't move to " + possibleMove + "!");
                    moves.remove(possibleMove);
                }

            }
            return moves;
        }
        return null;
    }

    public void checkMoveValidity(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();

        // case 0: there is no piece to move
        if (board.getPiece(start) == null) {
            throw new InvalidMoveException("there is no piece at " + start + " to move");
        }

        // case 1: it is not this team's turn
        if (board.getPiece(start).getTeamColor()!=team) {
            throw new InvalidMoveException("it is not this player's turn. " + team + " must make the next move.");
        }

        // case 3: it is not in the list of valid moves
        Collection<ChessMove> validityCheck = validMoves(start);
        boolean valid = false;
        for (ChessMove validMove : validityCheck) {
            if (move.equals(validMove)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new InvalidMoveException(move + " is not a valid move");
        }
    }

    public void movePiece(ChessMove move) {
        // check if the piece is just moving or if it's getting promoted
        ChessPiece pieceToMove;
        if (move.getPromotionPiece()!=null) {
            pieceToMove = new ChessPiece(team, move.getPromotionPiece());
        }
        else {
            pieceToMove = board.getPiece(move.getStartPosition());
        }

        // remove the piece from the old spot and add it to the new spot
        board.removePiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), pieceToMove);
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        try {
            // 1. check if the move is valid
            checkMoveValidity(move);

            // 2. move the piece
            movePiece(move);

            // 3. change which team's turn it is
            if (team==TeamColor.WHITE) {
                this.setTeamTurn(TeamColor.BLACK);
            } else {
                this.setTeamTurn(TeamColor.WHITE);
            }
        }
        catch(InvalidMoveException e) {
            throw e;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // find the position of teamColor's king
        ChessPosition kingPosition = board.findPiecePosition(ChessPiece.PieceType.KING, teamColor);
        if (kingPosition==null) {
            System.out.println("Can't find the king of " + teamColor + "!!");
            return true;
        }

        // go through the whole board, looking for enemy pieces and seeing if they can take the king
        for (int row=1; row<=8; row++) {
            for (int col=1; col<=8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                if (board.getPiece(position)!=null) {
                    if (board.getPiece(position).getTeamColor()!=teamColor) {
                        ChessPiece enemyPiece = board.getPiece(position);
                        Collection<ChessMove> enemyMoves = enemyPiece.pieceMoves(board, position);
                        for (ChessMove move : enemyMoves) {
                            ChessPosition destination = move.getEndPosition();
                            if (destination.equals(kingPosition)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


    // Main For Testing
    public static void main(String[] args) {
        ChessBoard gameboard = new ChessBoard();
        gameboard.addPiece((new ChessPosition(2,2)), (new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING)));
        gameboard.addPiece((new ChessPosition(4,4)), (new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK)));
        gameboard.addPiece((new ChessPosition(8,8)), (new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN)));
        gameboard.addPiece((new ChessPosition(8,1)), (new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING)));

        ChessGame game = new ChessGame(gameboard);

        gameboard.printBoard();

        Collection<ChessMove> vmoves = game.validMoves(new ChessPosition(4,4));
        for (ChessMove move : vmoves) {
            System.out.println(move);
        }

//        ChessMove move = new ChessMove((new ChessPosition(2,1)), (new ChessPosition(3,4)));
//        try {
//            game.makeMove(move);
//        }
//        catch(InvalidMoveException e) {
//            System.out.println("no");
//        }
//        gameboard.printBoard();


    }
}
