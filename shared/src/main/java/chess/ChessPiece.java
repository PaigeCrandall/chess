package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import chess.piecemoves.RookMovesStrategy;
import chess.piecemoves.BishopMovesStrategy;
import chess.piecemoves.QueenMovesStrategy;
import chess.piecemoves.KnightMovesStrategy;
import chess.piecemoves.KingMovesStrategy;

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
        this.strategy = Strategies.get(type);
    }

    public static final Map<PieceType, MoveStrategy> Strategies = Map.of(
//            PieceType.PAWN, new PawnMovesStrategy(),
            PieceType.ROOK, new chess.piecemoves.RookMovesStrategy(),
            PieceType.KNIGHT, new KnightMovesStrategy(),
            PieceType.BISHOP, new chess.piecemoves.BishopMovesStrategy(),
            PieceType.QUEEN, new chess.piecemoves.QueenMovesStrategy(),
            PieceType.KING, new KingMovesStrategy()
    );

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type && Objects.equals(strategy, that.strategy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type, strategy);
    }

    public String getLetter() {
        if (type == ChessPiece.PieceType.KNIGHT) {
            return "N";
        } else {
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

    public void printMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = pieceMoves(board, myPosition);
        for (ChessMove move : moves) {
            System.out.printf("%s, ", move.toString());
        }
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

}