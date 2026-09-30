package chess.piecemoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;


public class QueenMovesStrategy extends chess.CalculateMoves {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        RookMovesStrategy RookStrat = new RookMovesStrategy();
        BishopMovesStrategy BishopStrat = new BishopMovesStrategy();

        Collection<ChessMove> RookMoves = RookStrat.calculateMoves(board, startPosition);
        Collection<ChessMove> BishopMoves = BishopStrat.calculateMoves(board, startPosition);

        moves.addAll(RookMoves);
        moves.addAll(BishopMoves);

        return moves;
    }
}
