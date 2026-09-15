package chess.piecemoves;

import chess.ChessGame;
import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesStrategy implements ChessPiece.MoveStrategy {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        // white pieces
        if (board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            // normal move
            ChessPosition stepPosition = new ChessPosition(startPosition.getRow() + 1, startPosition.getColumn());
            if(board.getPiece(stepPosition)==null) {
                if (stepPosition.getRow() == 8) { //promotion
                    moves.add(new ChessMove(startPosition, stepPosition, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(startPosition, stepPosition, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(startPosition, stepPosition, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(startPosition, stepPosition, ChessPiece.PieceType.ROOK));
                } else {
                    moves.add(new ChessMove(startPosition, stepPosition));
                }
            }

            // if it's the first move
            if (startPosition.getRow() == 2) {
                ChessPosition jumpPosition = new ChessPosition(startPosition.getRow() + 2, startPosition.getColumn());
                if ((board.getPiece(jumpPosition)==null)&&(board.getPiece(stepPosition)==null)) {
                    moves.add(new ChessMove(startPosition, jumpPosition));
                }
            }

            // capture move
            if (startPosition.getColumn()+1<=8) {
                ChessPosition rightDiagonal = new ChessPosition(startPosition.getRow()+1, startPosition.getColumn()+1);
                if (board.getPiece(rightDiagonal)!=null) {
                    if(board.getPiece(rightDiagonal).getTeamColor()!=board.getPiece(startPosition).getTeamColor()) {
                        if (rightDiagonal.getRow() == 8) { //promotion
                            moves.add(new ChessMove(startPosition, rightDiagonal, ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(startPosition, rightDiagonal, ChessPiece.PieceType.BISHOP));
                            moves.add(new ChessMove(startPosition, rightDiagonal, ChessPiece.PieceType.KNIGHT));
                            moves.add(new ChessMove(startPosition, rightDiagonal, ChessPiece.PieceType.ROOK));
                        } else {
                            moves.add(new ChessMove(startPosition, rightDiagonal));
                        }
                    }
                }
            }
            if (startPosition.getColumn()-1>0) {
                ChessPosition leftDiagonal = new ChessPosition(startPosition.getRow() + 1, startPosition.getColumn() - 1);
                if (board.getPiece(leftDiagonal) != null) {
                    if(board.getPiece(leftDiagonal).getTeamColor()!=board.getPiece(startPosition).getTeamColor()) {
                        if (leftDiagonal.getRow() == 8) { //promotion
                            moves.add(new ChessMove(startPosition, leftDiagonal, ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(startPosition, leftDiagonal, ChessPiece.PieceType.BISHOP));
                            moves.add(new ChessMove(startPosition, leftDiagonal, ChessPiece.PieceType.KNIGHT));
                            moves.add(new ChessMove(startPosition, leftDiagonal, ChessPiece.PieceType.ROOK));
                        } else {
                            moves.add(new ChessMove(startPosition, leftDiagonal));
                        }
                    }
                }
            }

        }
        // black pieces
        else if (board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            // normal move
            ChessPosition stepPosition = new ChessPosition(startPosition.getRow() - 1, startPosition.getColumn());
            if(board.getPiece(stepPosition)==null) {
                if (stepPosition.getRow() == 1) { //promotion
                    moves.add(new ChessMove(startPosition, stepPosition, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(startPosition, stepPosition, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(startPosition, stepPosition, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(startPosition, stepPosition, ChessPiece.PieceType.ROOK));
                } else {
                    moves.add(new ChessMove(startPosition, stepPosition));
                }
            }

            // if it's the first move
            if (startPosition.getRow() == 7) {
                ChessPosition jumpPosition = new ChessPosition(startPosition.getRow()-2, startPosition.getColumn());
                if ((board.getPiece(jumpPosition)==null)&&(board.getPiece(stepPosition)==null)) {
                    moves.add(new ChessMove(startPosition, jumpPosition));
                }
            }

            // capture move
            if (startPosition.getColumn()-1>0) {
                ChessPosition rightDiagonal = new ChessPosition(startPosition.getRow()-1, startPosition.getColumn()-1);
                if (board.getPiece(rightDiagonal)!=null) {
                    if(board.getPiece(rightDiagonal).getTeamColor()!=board.getPiece(startPosition).getTeamColor()) {
                        if (rightDiagonal.getRow() == 1) { //promotion
                            moves.add(new ChessMove(startPosition, rightDiagonal, ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(startPosition, rightDiagonal, ChessPiece.PieceType.BISHOP));
                            moves.add(new ChessMove(startPosition, rightDiagonal, ChessPiece.PieceType.KNIGHT));
                            moves.add(new ChessMove(startPosition, rightDiagonal, ChessPiece.PieceType.ROOK));
                        } else {
                            moves.add(new ChessMove(startPosition, rightDiagonal));
                        }
                    }
                }
            }
            if (startPosition.getColumn()+1<=8) {
                ChessPosition leftDiagonal = new ChessPosition(startPosition.getRow() - 1, startPosition.getColumn() + 1);
                if (board.getPiece(leftDiagonal) != null) {
                    if(board.getPiece(leftDiagonal).getTeamColor()!=board.getPiece(startPosition).getTeamColor()) {
                        if (leftDiagonal.getRow() == 1) { //promotion
                            moves.add(new ChessMove(startPosition, leftDiagonal, ChessPiece.PieceType.QUEEN));
                            moves.add(new ChessMove(startPosition, leftDiagonal, ChessPiece.PieceType.BISHOP));
                            moves.add(new ChessMove(startPosition, leftDiagonal, ChessPiece.PieceType.KNIGHT));
                            moves.add(new ChessMove(startPosition, leftDiagonal, ChessPiece.PieceType.ROOK));
                        } else {
                            moves.add(new ChessMove(startPosition, leftDiagonal));
                        }
                    }
                }
            }

        }

            return moves;
        }
    }