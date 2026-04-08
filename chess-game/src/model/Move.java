package model;

public class Move {
    private final Position from;
    private final Position to;
    private final MoveType moveType;
    private final PieceType promotionPieceType;

    public Move(Position from, Position to) {
        this(from, to, MoveType.NORMAL, null);
    }

    public Move(Position from, Position to, MoveType moveType, PieceType promotionPieceType) {
        this.from = from;
        this.to = to;
        this.moveType = moveType;
        this.promotionPieceType = promotionPieceType;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public PieceType getPromotionPieceType() {
        return promotionPieceType;
    }
}
