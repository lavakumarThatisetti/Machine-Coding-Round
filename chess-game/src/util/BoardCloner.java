package util;

import model.piece.PieceFactory;
import model.Board;
import model.Position;
import model.piece.Piece;

public final class BoardCloner {
    private BoardCloner() {}

    public static Board clone(Board original) {
        Board copy = new Board();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position position = new Position(row, col);
                Piece piece = original.getPiece(position);
                if (piece != null) {
                    copy.setPiece(position, PieceFactory.copy(piece));
                }
            }
        }
        return copy;
    }
}
