import model.*;
import service.ChessGameService;
import validator.MoveValidator;

public class Main {
    public static void main(String[] args) {
        Player white = new Player("P1", "Alice", Color.WHITE);
        Player black = new Player("P2", "Bob", Color.BLACK);

        ChessGameService game = new ChessGameService(white, black, new MoveValidator());

        MoveResult result1 = game.makeMove(new Move(new Position(6, 4), new Position(4, 4))); // e2 -> e4
        System.out.println(result1.getMessage());

        MoveResult result2 = game.makeMove(new Move(new Position(1, 4), new Position(3, 4))); // e7 -> e5
        System.out.println(result2.getMessage());

        MoveResult result3 = game.makeMove(new Move(new Position(7, 6), new Position(5, 5))); // knight g1 -> f3
        System.out.println(result3.getMessage());

        System.out.println("Current turn: " + game.getCurrentTurn());
        System.out.println("Status: " + game.getStatus());
    }
}