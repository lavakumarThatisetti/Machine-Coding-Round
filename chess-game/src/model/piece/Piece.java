package model.piece;

import model.Board;
import model.Color;
import model.PieceType;
import model.Position;

public abstract class Piece {
    private final Color color;
    private final PieceType type;
    private boolean moved;

    protected Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
        this.moved = false;
    }

    public Color getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    public boolean hasMoved() {
        return moved;
    }

    public void markMoved() {
        this.moved = true;
    }

    public abstract boolean isValidBasicMove(Board board, Position from, Position to);

    public boolean isOpponent(Piece other) {
        return other != null && this.color != other.color;
    }
}
