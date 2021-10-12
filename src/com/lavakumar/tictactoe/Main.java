package com.lavakumar.tictactoe;

import com.lavakumar.tictactoe.model.Player;
import com.lavakumar.tictactoe.service.PlayTicTacToe;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        String player1 = scan.next();
        String player2 = scan.next();

        ArrayList<Player> players =new ArrayList<>();
        players.add(new Player(player1,1,"X"));
        players.add(new Player(player2,2,"O"));


        PlayTicTacToe playTicTacToe = new PlayTicTacToe(players,3);
        System.out.println("Win: "+playTicTacToe.PlayGame());
    }
}
