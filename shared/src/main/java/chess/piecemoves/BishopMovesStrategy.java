package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesStrategy implements ChessPiece.MoveStrategy {

    int addMove(ChessBoard board, ChessPosition startPosition, ChessPosition newPosition, Collection<ChessMove> moves) {
        if(board.getPiece(newPosition)==null) {
            ChessMove move = new ChessMove(startPosition, newPosition);
            moves.add(move);
            return 1;
        }
        else if (board.getPiece(newPosition).getTeamColor()!=board.getPiece(startPosition).getTeamColor()) {
            ChessMove move = new ChessMove(startPosition, newPosition);
            moves.add(move);
            return 0;
        }
        else {
            return 0;
        }
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        //Right diagonal

        // Left diagonal
        return moves;
    }
}
