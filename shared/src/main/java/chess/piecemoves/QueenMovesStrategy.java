package chess.piecemoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;


public class QueenMovesStrategy extends chess.CalculateMoves {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        RookMovesStrategy rookStrat = new RookMovesStrategy();
        BishopMovesStrategy bishopStrat = new BishopMovesStrategy();

        Collection<ChessMove> rookMoves = rookStrat.calculateMoves(board, startPosition);
        Collection<ChessMove> bishopMoves = bishopStrat.calculateMoves(board, startPosition);

        moves.addAll(rookMoves);
        moves.addAll(bishopMoves);

        return moves;
    }
}
