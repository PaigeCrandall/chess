package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesStrategy implements ChessPiece.MoveStrategy {

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
        // horizontal
        for (int x = startPosition.getColumn()+1; x <= 8; x++) {
            ChessPosition newPosition = new ChessPosition(startPosition.getRow(), x);
            int can_continue = addMove(board, startPosition, newPosition, moves);
            if (can_continue==0) { break; }
        }
        for (int x = startPosition.getColumn()-1; x > 0; x--) {
            ChessPosition newPosition = new ChessPosition(startPosition.getRow(), x);
            int can_continue = addMove(board, startPosition, newPosition, moves);
            if (can_continue == 0) { break; }
        }
        // vertical
        for (int y = startPosition.getRow()-1; y > 0; y--) {
            ChessPosition newPosition = new ChessPosition(y, startPosition.getColumn());
            int can_continue = addMove(board, startPosition, newPosition, moves);
            if (can_continue == 0) { break; }
        }
        for (int y = startPosition.getRow()+1; y <= 8; y++) {
            ChessPosition newPosition = new ChessPosition(y, startPosition.getColumn());
            int can_continue = addMove(board, startPosition, newPosition, moves);
            if (can_continue == 0) { break; }
        }
        return moves;
    }
}
