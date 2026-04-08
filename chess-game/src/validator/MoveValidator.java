package validator;

import model.*;
import model.piece.Piece;
import util.BoardCloner;

public class MoveValidator {

    public boolean isValidMove(Board board, Move move, Color currentTurn) {
        Position from = move.getFrom();
        Position to = move.getTo();

        Piece piece = board.getPiece(from);
        if (piece == null) {
            return false;
        }

        if (piece.getColor() != currentTurn) {
            return false;
        }

        Piece destinationPiece = board.getPiece(to);
        if (destinationPiece != null && destinationPiece.getColor() == piece.getColor()) {
            return false;
        }

        if (!piece.isValidBasicMove(board, from, to)) {
            return false;
        }

        // extra rule: move should not leave own king in check
        return !wouldLeaveKingInCheck(board, move, currentTurn);
    }

    private boolean wouldLeaveKingInCheck(Board board, Move move, Color color) {
        Board cloned = BoardCloner.clone(board);
        cloned.movePiece(move.getFrom(), move.getTo());
        return isKingInCheck(cloned, color);
    }

    public boolean isKingInCheck(Board board, Color color) {
        Position kingPosition = findKing(board, color);
        Color opponent = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position from = new Position(row, col);
                Piece piece = board.getPiece(from);
                if (piece == null || piece.getColor() != opponent) {
                    continue;
                }
                if (piece.isValidBasicMove(board, from, kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Position findKing(Board board, Color color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Position(row, col));
                if (piece != null && piece.getType() == PieceType.KING && piece.getColor() == color) {
                    return new Position(row, col);
                }
            }
        }
        throw new IllegalStateException("King not found");
    }
}
