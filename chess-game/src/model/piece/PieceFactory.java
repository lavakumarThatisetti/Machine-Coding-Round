package model.piece;

import model.Color;
import model.PieceType;

public final class PieceFactory {
    private PieceFactory() {}

    public static Piece create(PieceType type, Color color) {
        return switch (type) {
            case KING -> new King(color);
            case QUEEN -> new Queen(color);
            case ROOK -> new Rook(color);
            case BISHOP -> new Bishop(color);
            case KNIGHT -> new Knight(color);
            case PAWN -> new Pawn(color);
        };
    }

    public static Piece copy(Piece piece) {
        Piece cloned = create(piece.getType(), piece.getColor());
        if (piece.hasMoved()) {
            cloned.markMoved();
        }
        return cloned;
    }
}
