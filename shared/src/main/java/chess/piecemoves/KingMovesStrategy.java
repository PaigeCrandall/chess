package chess.piecemoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesStrategy extends chess.CalculateMoves {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        // forward and back
        addMove(board, moves, startPosition, startPosition.getColumn(), startPosition.getRow()+1);
        addMove(board, moves, startPosition, startPosition.getColumn(), startPosition.getRow()-1);
        // left and right
        addMove(board, moves, startPosition, startPosition.getColumn()+1, startPosition.getRow());
        addMove(board, moves, startPosition, startPosition.getColumn()-1, startPosition.getRow());
        // diagonals
        addMove(board, moves, startPosition, startPosition.getColumn()+1, startPosition.getRow()+1);
        addMove(board, moves, startPosition, startPosition.getColumn()+1, startPosition.getRow()-1);
        addMove(board, moves, startPosition, startPosition.getColumn()-1, startPosition.getRow()-1);
        addMove(board, moves, startPosition, startPosition.getColumn()-1, startPosition.getRow()+1);
        return moves;
    }
}