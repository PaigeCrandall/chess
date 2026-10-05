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

    public TeamColor getOpposingTeamTurn(TeamColor color) {
        if (color==TeamColor.WHITE) {
            return TeamColor.BLACK;
        } else {
            return TeamColor.WHITE;
        }
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    public void switchTeamTurn(TeamColor team) {
        setTeamTurn(getOpposingTeamTurn(team));
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Check a position on the board to see it there is a piece there of TeamColor @param team
     */
    public boolean checkBoardPosition(ChessPosition positionToCheck, TeamColor team) {
        if (board.getPiece(positionToCheck)==null) {
            return false;
        }
        return board.getPiece(positionToCheck).getTeamColor() == team;
    }

    public boolean moveCausesCheck(ChessMove possibleMove) {
        // for each move, imagine if the piece moved there
        ChessBoard imaginaryBoard = board.copy();
        ChessGame imaginaryGame = new ChessGame(imaginaryBoard);
        imaginaryGame.movePiece(possibleMove);

        // check if your king is in check in the new arrangement
        TeamColor color = board.getPiece(possibleMove.getStartPosition()).getTeamColor();
        return imaginaryGame.isInCheck(color);
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

            // get the piece's possible moves from it's strategy
            ChessPiece piece = board.getPiece(startPosition);
            Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

            // check for check :)
            for (ChessMove possibleMove : piece.pieceMoves(board, startPosition)) {
                if (moveCausesCheck(possibleMove)) { moves.remove(possibleMove); }
            }
            return moves;
        }
        return null;
    }

    public boolean isInValidMovesList(ChessMove move) {
        Collection<ChessMove> validityCheck = validMoves(move.getStartPosition());
        boolean valid = false;
        for (ChessMove validMove : validityCheck) {
            if (move.equals(validMove)) {
                valid = true;
                break;
            }
        }
        return valid;
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
        if (!isInValidMovesList(move)) {
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
            checkMoveValidity(move);

            movePiece(move);

            switchTeamTurn(team);
        }
        catch(InvalidMoveException e) {
            throw e;
        }
    }

    public boolean pieceCanKillKing(ChessPosition kingPosition, ChessPosition squareToCheck, TeamColor teamColor) {
        if (checkBoardPosition(squareToCheck, getOpposingTeamTurn(teamColor))) {
            ChessPiece enemyPiece = board.getPiece(squareToCheck);
            Collection<ChessMove> enemyMoves = enemyPiece.pieceMoves(board, squareToCheck);
            for (ChessMove move : enemyMoves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
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

        for (int row=1; row<=8; row++) {
            for (int col=1; col<=8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                if (pieceCanKillKing(kingPosition, new ChessPosition(row, col), teamColor)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean teamCannotMove(TeamColor teamColor) {
        for (int row=1; row<=8; row++) {
            for (int col=1; col<=8; col++) {
                ChessPosition square = new ChessPosition(row, col);
                if (checkBoardPosition(square, teamColor)) {
                    Collection<ChessMove> possibleMoves = validMoves(square);

                    if (!possibleMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && teamCannotMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return teamCannotMove(teamColor);
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

}
