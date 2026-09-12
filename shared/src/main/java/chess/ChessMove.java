package chess;

/**
 * Represents moving a chess piece on a chessboard
 */
public class ChessMove {
    ChessPosition start;
    ChessPosition end;
    ChessPiece.PieceType promoted;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.start = startPosition;
        this.end = endPosition;
        this.promoted = promotionPiece;
    }

    public ChessPosition getStartPosition() {
        return start;
    }

    public ChessPosition getEndPosition() {
        return end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promoted;
    }
}
