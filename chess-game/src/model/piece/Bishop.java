package model.piece;

import model.Board;
import model.Color;
import model.PieceType;
import model.Position;

public class Bishop extends Piece {
    public Bishop(Color color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public boolean isValidBasicMove(Board board, Position from, Position to) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());

        if (rowDiff != colDiff) {
            return false;
        }
        return board.isPathClear(from, to);
    }
}
