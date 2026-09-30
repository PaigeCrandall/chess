package chess.piecemoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesStrategy extends chess.CalculateMoves {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        //Right diagonal
        for (int x = startPosition.getColumn()+1, y = startPosition.getRow()+1; (x <= 8) && (y <= 8); x++, y++) {
            int can_continue = addMove(board, moves, startPosition, x, y);
            if (can_continue!=1) { break; }
        }
        for (int x = startPosition.getColumn()-1, y = startPosition.getRow()-1; (x > 0) && (y > 0); x--, y--) {
            int can_continue = addMove(board, moves, startPosition, x, y);
            if (can_continue!=1) { break; }
        }
        // Left diagonal
        for (int x = startPosition.getColumn()-1, y = startPosition.getRow()+1; (x > 0) && (y <= 8); x--, y++) {
            int can_continue = addMove(board, moves, startPosition, x, y);
            if (can_continue!=1) { break; }
        }
        for (int x = startPosition.getColumn()+1, y = startPosition.getRow()-1; (x <= 8) && (y > 0); x++, y--) {
            int can_continue = addMove(board, moves, startPosition, x, y);
            if (can_continue!=1) { break; }
        }
        return moves;
    }
}
