package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;


public class QueenMovesStrategy implements ChessPiece.MoveStrategy {
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
