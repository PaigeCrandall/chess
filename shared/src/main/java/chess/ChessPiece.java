package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor color;
    ChessPiece.PieceType type;
    MoveStrategy strategy;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
        this.strategy = new RookMovesStrategy();
    }

//    public static final Map<PieceType, MoveStrategy> Strategies = Map.of(
//            PieceType.PAWN, new PawnMoveStrategy(),
//            PieceType.ROOK, new RookMoveStrategy(),
//            PieceType.KNIGHT, new RookMoveStrategy(),
//            PieceType.BISHOP, new RookMoveStrategy(),
//            PieceType.QUEEN, new RookMoveStrategy(),
//            PieceType.KING, new RookMoveStrategy()
//    );

    public String getLetter() {
        if (type==ChessPiece.PieceType.KNIGHT) {
            return "N";
        }
        else {
            return String.valueOf(type.name().charAt(0));
        }
    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return strategy.calculateMoves(board, myPosition);
    }

    public interface MoveStrategy {
        Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition);
    }

    private static class RookMovesStrategy implements MoveStrategy {
        @Override
        public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition) {
            Collection<ChessMove> moves = new ArrayList<>();
            // vertical
            // they can both go either way, but we need to check the bounds
            // current vertical position == position.getRow();
            // out of bounds is anything outside 0 < y <=8
            for (int y = startPosition.getRow(); y <= 8; y++) {
                ChessPosition newPosition = new ChessPosition(startPosition.getColumn(), y);
                ChessMove move = new ChessMove(startPosition, newPosition);
                moves.add(move);
            }
            for (int y = startPosition.getRow(); y > 0; y--) {
                ChessPosition newPosition = new ChessPosition(startPosition.getColumn(), y);
                ChessMove move = new ChessMove(startPosition, newPosition);
                moves.add(move);
            }
            // horizontal
            for (int x = startPosition.getColumn(); x <= 8; x++) {
                ChessPosition newPosition = new ChessPosition(startPosition.getColumn(), x);
                ChessMove move = new ChessMove(startPosition, newPosition);
                moves.add(move);
            }
            for (int x = startPosition.getRow(); x > 0; x--) {
                ChessPosition newPosition = new ChessPosition(startPosition.getColumn(), x);
                ChessMove move = new ChessMove(startPosition, newPosition);
                moves.add(move);
            }
            return moves;
        }
    }
}
