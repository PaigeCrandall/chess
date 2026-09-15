package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesStrategy implements ChessPiece.MoveStrategy {

    void addMove(ChessBoard board, ChessPosition startPosition, Collection<ChessMove> moves, int x, int y) {
        if ((x>0)&&(x<=8)&&(y>0)&&(y<=8)) {
            ChessPosition newPosition = new ChessPosition(y, x);
            if(board.getPiece(newPosition)==null) {
                ChessMove move = new ChessMove(startPosition, newPosition);
                moves.add(move);
            }
            else if (board.getPiece(newPosition).getTeamColor()!=board.getPiece(startPosition).getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, newPosition);
                moves.add(move);
            }
        }
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        // forward and back
        addMove(board, startPosition, moves, startPosition.getColumn(), startPosition.getRow()+1);
        addMove(board, startPosition, moves, startPosition.getColumn(), startPosition.getRow()-1);
        // left and right
        addMove(board, startPosition, moves, startPosition.getColumn()+1, startPosition.getRow());
        addMove(board, startPosition, moves, startPosition.getColumn()-1, startPosition.getRow());
        // diagonals
        addMove(board, startPosition, moves, startPosition.getColumn()+1, startPosition.getRow()+1);
        addMove(board, startPosition, moves, startPosition.getColumn()+1, startPosition.getRow()-1);
        addMove(board, startPosition, moves, startPosition.getColumn()-1, startPosition.getRow()-1);
        addMove(board, startPosition, moves, startPosition.getColumn()-1, startPosition.getRow()+1);
        return moves;
    }
}