package service;

import model.Board;
import model.Color;
import model.Move;
import model.Position;
import model.piece.Piece;
import validator.MoveValidator;

import java.util.ArrayList;
import java.util.List;

public class LegalMoveGenerator {
    public List<Move> generateAllLegalMoves(Board board, Color color, MoveValidator validator) {
        List<Move> moves = new ArrayList<>();

        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                Position from = new Position(fromRow, fromCol);
                Piece piece = board.getPiece(from);
                if (piece == null || piece.getColor() != color) {
                    continue;
                }

                for (int toRow = 0; toRow < 8; toRow++) {
                    for (int toCol = 0; toCol < 8; toCol++) {
                        Move move = new Move(from, new Position(toRow, toCol));
                        if (validator.isValidMove(board, move, color)) {
                            moves.add(move);
                        }
                    }
                }
            }
        }

        return moves;
    }
}
