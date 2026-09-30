package chess;

import chess.ChessPiece.MoveStrategy;

import java.util.Collection;

public abstract class CalculateMoves implements MoveStrategy {

    public int canCaptureNormal() {
        return 1;
    }
    public int stoppedBySameColor() {
        return 1;
    }


    // returns 1 if the piece was not stopped
    // returns 0 if the piece was stopped
    public int addMove(ChessBoard board, Collection<ChessMove> moves, ChessPosition startPosition, int new_x, int new_y) {
        if ((new_x>8) || (new_x<1) || (new_y>8) || (new_y<1)) {
            // out of bounds
            return 0;
        }
        ChessPosition newPosition = new ChessPosition(new_y, new_x);
        if (board.getPiece(newPosition)==null) {
            // there is not a chess piece there
            ChessMove newMove = new ChessMove(startPosition, newPosition);
            moves.add(newMove);
            return 1;
        }
        else if (board.getPiece(newPosition).getTeamColor()==board.getPiece(startPosition).getTeamColor()) {
            // there is a chess piece of the same color there
            if (stoppedBySameColor()==1) {
                return 0;
            }
            else {
                return 1;
            }
        }
        else if (board.getPiece(newPosition).getTeamColor()!=board.getPiece(startPosition).getTeamColor()) {
            // there is a chess piece of the other color there
            if (canCaptureNormal()==1) {
                ChessMove newMove = new ChessMove(startPosition, newPosition);
                moves.add(newMove);
                return 0;
            }
        }
        return 0;
    }

    public abstract Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition);

}
