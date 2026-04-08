package model;

import model.piece.Piece;

public class Board {
    private final Piece[][] grid;

    public Board() {
        this.grid = new Piece[8][8];
    }

    public Piece getPiece(Position position) {
        return grid[position.getRow()][position.getCol()];
    }

    public void setPiece(Position position, Piece piece) {
        grid[position.getRow()][position.getCol()] = piece;
    }

    public void movePiece(Position from, Position to) {
        Piece piece = getPiece(from);
        setPiece(to, piece);
        setPiece(from, null);
        if (piece != null) {
            piece.markMoved();
        }
    }

    public boolean isEmpty(Position position) {
        return getPiece(position) == null;
    }

    public boolean isInside(Position position) {
        return position.getRow() >= 0 && position.getRow() < 8
                && position.getCol() >= 0 && position.getCol() < 8;
    }

    public boolean isPathClear(Position from, Position to) {
        int rowStep = Integer.compare(to.getRow(), from.getRow());
        int colStep = Integer.compare(to.getCol(), from.getCol());

        int currentRow = from.getRow() + rowStep;
        int currentCol = from.getCol() + colStep;

        while (currentRow != to.getRow() || currentCol != to.getCol()) {
            if (grid[currentRow][currentCol] != null) {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        return true;
    }
}
