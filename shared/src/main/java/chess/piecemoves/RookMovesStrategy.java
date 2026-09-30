package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesStrategy extends chess.CalculateMoves {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        // horizontal
        for (int x = startPosition.getColumn()+1; x <= 8; x++) {
//            ChessPosition newPosition = new ChessPosition(startPosition.getRow(), x);
            int can_continue = addMove(board, moves, startPosition, x, startPosition.getRow());
            if (can_continue==0) { break; }
        }
        for (int x = startPosition.getColumn()-1; x > 0; x--) {
//            ChessPosition newPosition = new ChessPosition(startPosition.getRow(), x);
            int can_continue = addMove(board, moves, startPosition, x, startPosition.getRow());
            if (can_continue == 0) { break; }
        }
        // vertical
        for (int y = startPosition.getRow()-1; y > 0; y--) {
//            ChessPosition newPosition = new ChessPosition(y, startPosition.getColumn());
            int can_continue = addMove(board, moves, startPosition, startPosition.getColumn(), y);
            if (can_continue == 0) { break; }
        }
        for (int y = startPosition.getRow()+1; y <= 8; y++) {
            int can_continue = addMove(board, moves, startPosition, startPosition.getColumn(), y);
            if (can_continue == 0) { break; }
        }
        return moves;
    }
}
