package com.lavakumar.tictactoe;



import com.lavakumar.tictactoe.mode.ConsolePrint;
import com.lavakumar.tictactoe.model.Board;
import com.lavakumar.tictactoe.model.Player;
import com.lavakumar.tictactoe.service.TicTacToeService;
import com.lavakumar.tictactoe.validations.GameValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main_Another {
    public static void main(String[] args) {
        Board board =  new Board(3);

        Player player1 = new Player("Player1",1,"O");
        Player player2 = new Player("Player2",2,"X");
        List<Player> players = Arrays.asList(player1, player2);
        HashMap<Integer,Boolean> playerCheck = new HashMap<>();
        playerCheck.put(player1.getId(), true);
        playerCheck.put(player2.getId(), true);
        GameValidator gameValidator = new GameValidator(board, playerCheck);
        TicTacToeService ticTacToeService = new TicTacToeService(board, players, gameValidator, new ConsolePrint());

        while (true) {
            System.out.println("Choose Input 1: play 2: Exit");

            Scanner scanner = new Scanner(System.in);
            int option = scanner.nextInt();
            switch (option){
                case 1:
                    System.out.println("Please Enter Your ID");
                    Player player = players.get(scanner.nextInt()-1);
                    System.out.println("Enter Your Move X and Y");
                    ticTacToeService.input(scanner.nextInt(),scanner.nextInt(),player);
                break;
                case 2: System.exit(0);
                break;
                default:
                    System.out.println("Choose 1");
                    break;
            }

        }


    }
}