package model;

import java.util.Objects;

public final class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            throw new IllegalArgumentException("Invalid board position");
        }
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean sameRow(Position other) {
        return this.row == other.row;
    }

    public boolean sameCol(Position other) {
        return this.col == other.col;
    }

    public int rowDiff(Position other) {
        return other.row - this.row;
    }

    public int colDiff(Position other) {
        return other.col - this.col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position position)) return false;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
