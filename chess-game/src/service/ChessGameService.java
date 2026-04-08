package service;

import model.*;
import model.piece.*;
import validator.MoveValidator;

import java.util.ArrayList;
import java.util.List;

public class ChessGameService {
    private final Board board;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private final MoveValidator moveValidator;
    private final List<Move> moveHistory;

    private Color currentTurn;
    private GameStatus status;

    public ChessGameService(Player whitePlayer, Player blackPlayer, MoveValidator moveValidator) {
        this.board = new Board();
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.moveValidator = moveValidator;
        this.moveHistory = new ArrayList<>();
        this.currentTurn = Color.WHITE;
        this.status = GameStatus.NOT_STARTED;
        initializeBoard();
        this.status = GameStatus.IN_PROGRESS;
    }

    public synchronized MoveResult makeMove(Move move) {
        if (status == GameStatus.CHECKMATE || status == GameStatus.STALEMATE || status == GameStatus.FINISHED) {
            return MoveResult.failure("Game already finished");
        }

        if (!moveValidator.isValidMove(board, move, currentTurn)) {
            return MoveResult.failure("Invalid move");
        }

        Piece destination = board.getPiece(move.getTo());
        boolean capture = destination != null;

        board.movePiece(move.getFrom(), move.getTo());
        moveHistory.add(move);

        switchTurn();
        updateStatus();

        return MoveResult.success(capture ? "Capture move executed" : "Move executed");
    }

    private void switchTurn() {
        currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private void updateStatus() {
        if (moveValidator.isKingInCheck(board, currentTurn)) {
            status = GameStatus.CHECK;
            // if time allows, compute checkmate
        } else {
            status = GameStatus.IN_PROGRESS;
        }
    }

    public Board getBoard() {
        return board;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public GameStatus getStatus() {
        return status;
    }

    public List<Move> getMoveHistory() {
        return List.copyOf(moveHistory);
    }

    private void initializeBoard() {
        // black
        board.setPiece(new Position(0, 0), new Rook(Color.BLACK));
        board.setPiece(new Position(0, 1), new Knight(Color.BLACK));
        board.setPiece(new Position(0, 2), new Bishop(Color.BLACK));
        board.setPiece(new Position(0, 3), new Queen(Color.BLACK));
        board.setPiece(new Position(0, 4), new King(Color.BLACK));
        board.setPiece(new Position(0, 5), new Bishop(Color.BLACK));
        board.setPiece(new Position(0, 6), new Knight(Color.BLACK));
        board.setPiece(new Position(0, 7), new Rook(Color.BLACK));
        for (int col = 0; col < 8; col++) {
            board.setPiece(new Position(1, col), new Pawn(Color.BLACK));
        }

        // white
        board.setPiece(new Position(7, 0), new Rook(Color.WHITE));
        board.setPiece(new Position(7, 1), new Knight(Color.WHITE));
        board.setPiece(new Position(7, 2), new Bishop(Color.WHITE));
        board.setPiece(new Position(7, 3), new Queen(Color.WHITE));
        board.setPiece(new Position(7, 4), new King(Color.WHITE));
        board.setPiece(new Position(7, 5), new Bishop(Color.WHITE));
        board.setPiece(new Position(7, 6), new Knight(Color.WHITE));
        board.setPiece(new Position(7, 7), new Rook(Color.WHITE));
        for (int col = 0; col < 8; col++) {
            board.setPiece(new Position(6, col), new Pawn(Color.WHITE));
        }
    }
}
