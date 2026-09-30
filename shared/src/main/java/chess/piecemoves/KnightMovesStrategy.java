package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesStrategy extends chess.CalculateMoves {

    @Override
    public int stoppedBySameColor() {
        return 0;
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // forward 2 sideways 1
        addMove(board, moves, startPosition, startPosition.getColumn()+1, startPosition.getRow()+2);
        addMove(board, moves, startPosition, startPosition.getColumn()-1, startPosition.getRow()+2);

        // backward 2 sideways 1
        addMove(board,  moves, startPosition, startPosition.getColumn()+1, startPosition.getRow()-2);
        addMove(board, moves, startPosition, startPosition.getColumn()-1, startPosition.getRow()-2);

        // right 2 forwad/back 1
        addMove(board, moves, startPosition, startPosition.getColumn()+2, startPosition.getRow()+1);
        addMove(board, moves, startPosition, startPosition.getColumn()+2, startPosition.getRow()-1);

        // left 2 forwad/back 1
        addMove(board, moves, startPosition, startPosition.getColumn()-2, startPosition.getRow()+1);
        addMove(board, moves, startPosition, startPosition.getColumn()-2, startPosition.getRow()-1);
        return moves;
    }
}
