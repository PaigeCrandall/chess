package chess.piecemoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesStrategy extends chess.CalculateMoves {

    public int canCaptureNormal() {
        return 0;
    }

    //returns 1 if promoted, 0 otherwise
    public void addPromotionMoves(Collection<ChessMove> moves, ChessPosition startPosition, ChessPosition stepPosition) {
        moves.add(new ChessMove(startPosition, stepPosition, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(startPosition, stepPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(startPosition, stepPosition, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(startPosition, stepPosition, ChessPiece.PieceType.ROOK));
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        // set step direction
        int stepDirection = 1;
        if (board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            stepDirection = -1;
        }
        // set first row
        int firstRow = 2;
        if (board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            firstRow = 7;
        }
        // set last row
        int lastRow = 8;
        if (board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            lastRow = 1;
        }


        // normal move
        ChessPosition stepPosition = new ChessPosition(startPosition.getRow() + (stepDirection), startPosition.getColumn());
        if (stepPosition.getRow() == lastRow) { // worthy of promotion
            addPromotionMoves(moves, startPosition, stepPosition);
        }
        else {
            addMove(board, moves, startPosition, startPosition.getColumn(), startPosition.getRow()+stepDirection);
        }

        // if it's the first move
        if ((startPosition.getRow() == firstRow)&&(board.getPiece(stepPosition)==null)) {
            addMove(board, moves, startPosition, startPosition.getColumn(), startPosition.getRow()+(2*stepDirection));
        }

        // capture move
        if ((startPosition.getColumn()+stepDirection<=8) && (startPosition.getColumn()+stepDirection>0)) {
            ChessPosition rightDiagonal = new ChessPosition(startPosition.getRow()+stepDirection, startPosition.getColumn()+stepDirection);
            if (board.getPiece(rightDiagonal)!=null) {
                if(board.getPiece(rightDiagonal).getTeamColor()!=board.getPiece(startPosition).getTeamColor()) {
                    if (rightDiagonal.getRow() == lastRow) { //promotion
                        addPromotionMoves(moves, startPosition, rightDiagonal);
                    } else {
                        moves.add(new ChessMove(startPosition, rightDiagonal));
                    }
                }
            }
        }
        if ((startPosition.getColumn()-stepDirection<=8) && (startPosition.getColumn()-stepDirection>0)) {
            ChessPosition leftDiagonal = new ChessPosition(startPosition.getRow() + stepDirection, startPosition.getColumn() - stepDirection);
            if (board.getPiece(leftDiagonal) != null) {
                if(board.getPiece(leftDiagonal).getTeamColor()!=board.getPiece(startPosition).getTeamColor()) {
                    if (leftDiagonal.getRow() == lastRow) { //promotion
                        addPromotionMoves(moves, startPosition, leftDiagonal);
                    } else {
                        moves.add(new ChessMove(startPosition, leftDiagonal));
                    }
                }
            }
        }

            return moves;
        }
    }