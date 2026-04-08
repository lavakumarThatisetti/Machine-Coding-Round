package model.piece;

import model.Board;
import model.Color;
import model.PieceType;
import model.Position;

public class Queen extends Piece {
    public Queen(Color color) {
        super(color, PieceType.QUEEN);
    }

    @Override
    public boolean isValidBasicMove(Board board, Position from, Position to) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());

        boolean straight = from.sameRow(to) || from.sameCol(to);
        boolean diagonal = rowDiff == colDiff;

        if (!straight && !diagonal) {
            return false;
        }
        return board.isPathClear(from, to);
    }
}
