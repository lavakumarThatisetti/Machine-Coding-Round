package model.piece;

import model.Board;
import model.Color;
import model.PieceType;
import model.Position;

public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public boolean isValidBasicMove(Board board, Position from, Position to) {
        int direction = (getColor() == Color.WHITE) ? -1 : 1;
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getCol() - from.getCol();

        Piece destination = board.getPiece(to);

        // forward move by 1
        if (colDiff == 0 && rowDiff == direction && destination == null) {
            return true;
        }

        // forward move by 2 on first move
        if (colDiff == 0 && rowDiff == 2 * direction && !hasMoved()) {
            Position mid = new Position(from.getRow() + direction, from.getCol());
            return board.getPiece(mid) == null && destination == null;
        }

        // diagonal capture
        return Math.abs(colDiff) == 1 && rowDiff == direction && destination != null && isOpponent(destination);
    }
}