package com.lavakumar.snakeandladder;

import com.lavakumar.snakeandladder.model.Entities;
import com.lavakumar.snakeandladder.service.PlaySnakeAndLadder;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Entities entities = Entities.getInstance();

        int noOfSnakes = scan.nextInt();
        while(noOfSnakes>0){
            int startPosition = scan.nextInt();
            int endPosition = scan.nextInt();
            entities.setSnake(startPosition, endPosition);
            noOfSnakes--;
        }
        int noOfLadders = scan.nextInt();
        while(noOfLadders>0){
            int startPosition = scan.nextInt();
            int endPosition = scan.nextInt();
            entities.setLadder(startPosition, endPosition);
            noOfLadders--;
        }
        int noOfPlayers = scan.nextInt();
        int i=0;
        while(noOfPlayers>0){
            String player = scan.next();
            entities.setPlayer(i++, player);
            noOfPlayers--;
        }
        PlaySnakeAndLadder playSnakeAndLadder = new PlaySnakeAndLadder(6);
        System.out.println("Win: "+playSnakeAndLadder.PlayGame());
    }
}
