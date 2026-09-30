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
    ChessBoard board = new ChessBoard();
    TeamColor team;

    public ChessGame() {
        this.board = new ChessBoard();
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
//            System.out.println(piece.getPieceType());
//            piece.printMoves(board, startPosition);
            Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
            // To Do: make sure your team is not in danger of check!!!
            return moves;
        }
        return null;
    }

    public void checkForValidity(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();

        // if there is no piece to move
        if (board.getPiece(start) == null) {
            throw new InvalidMoveException("there is no piece at " + start.toString() + " to move");
        }

        // if the piece I'm trying to move is not the right color
        if (board.getPiece(start).getTeamColor()!=team) {
            throw new InvalidMoveException("it is not this player's turn. " + team + " must make the next move.");
        }

        // check it's in the list of valid moves
        Collection<ChessMove> validityCheck = validMoves(start);
        boolean valid = false;
        for (ChessMove check : validityCheck) {
//            System.out.println("check: " + move.toString() + " " + check.toString());
            if (move.equals(check)) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            throw new InvalidMoveException(move.toString() + " is not a valid move");
        }
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        try {
            checkForValidity(move);

            // getting the piece to move to the new spot
            ChessPiece pieceToMove;
            if (move.getPromotionPiece()!=null) {
                pieceToMove = new ChessPiece(team, move.getPromotionPiece());
            }
            else {
                pieceToMove = board.getPiece(start);
            }

            // remove the piece from the old spot and add it to the new spot
            board.removePiece(start);
            board.addPiece(end, pieceToMove);

            // after the move, it is the next player's turn
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

        // go through the whole board
        // if the piece color is not teamColor
        // check their moveslist
        // if one of the moves ends on kingPosition, return true
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
        gameboard.resetBoard();
        ChessGame game = new ChessGame();
        game.setBoard(gameboard);
        gameboard.printBoard();
        game.validMoves(new ChessPosition(2,2));
    }
}
