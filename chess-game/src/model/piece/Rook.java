package model.piece;

import model.Board;
import model.Color;
import model.PieceType;
import model.Position;

public class Rook extends Piece {
    public Rook(Color color) {
        super(color, PieceType.ROOK);
    }

    @Override
    public boolean isValidBasicMove(Board board, Position from, Position to) {
        if (!from.sameRow(to) && !from.sameCol(to)) {
            return false;
        }
        return board.isPathClear(from, to);
    }
}
