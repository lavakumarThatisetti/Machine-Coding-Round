package com.lavakumar.tictactoe.service;

import com.lavakumar.tictactoe.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class PlayTicTacToe {
    HashMap<String, PairPosition> playerHistory;
    HashMap<String, Integer> playerLatestPosition;
    Board board ;
    List<Player> players;
    Scanner scan= new Scanner(System.in);
    public PlayTicTacToe(List<Player> player, int N){
        playerHistory = new HashMap<>();
        playerLatestPosition = new HashMap<>();
        board = new Board(N);
        this.players = player;

    }
    public String PlayGame(){
        board.printBoard();
        int i=0;
        while(true){
            if(i>=players.size()){
                i=0;
            }
            System.out.println(players.get(i).getPlayerName()+" Turn "+players.get(i).getSymbol());
            int x = scan.nextInt();
            int y = scan.nextInt();
            if(checkValidCordinates(x,y) && checPositionisEmpty(x,y) ){
                Player player = players.get(i);
                board.setposition(x,y,player.getSymbol());
                board.printBoard();
                if(checkboard(x,y,player.getSymbol())){
                    return players.get(i).getPlayerName();
                }
            }else{
                System.out.println("Invalid input");
            }
            i++;
        }
    }
    private boolean checkForPlayerWin(Player player){
        return player.getWin();
    }

    private boolean checkValidCordinates(int x, int y){
        return x < board.getBoard().length && y < board.getBoard().length;
    }
    private boolean checPositionisEmpty(int x, int y){
        return board.getposition(x,y).isEmpty();
    }

    private boolean checkboard(int row,int col,String symbol){
        boolean winRow = true, winCol = true, winLeft = true, winRight = true;
        for(int i=0;i<board.getBoard().length;i++){
            if(!board.getposition(row,i).equals(symbol)){
                winRow = false;
            }
            if(!board.getposition(i,col).equals(symbol)){
                winCol = false;
            }
            if(!board.getposition(i,i).equals(symbol)){
                winLeft = false;
            }
            if(!board.getposition(i,board.getBoard().length-i-1).equals(symbol)){
                winRight =false;
            }
        }
        return winRow || winColm || winLeft || winRight;
    }

    private boolean checkRowWise(int row,int col,String symbol){
        for(int i=0;i<board.getBoard().length;i++){
             if(!board.getposition(row,i).equals(symbol)) return false;
        }
        return true;
    }
    private boolean checkColumnWise(int row,int col,String symbol){
        for(int i=0;i<board.getBoard().length;i++){
            if(!board.getposition(i,col).equals(symbol)) return false;
        }
        return true;
    }
    private boolean checkleftDiagonal(int row,int col,String symbol){
        if(row != col) return false;
        for(int i=0;i<board.getBoard().length;i++){
            if(!board.getposition(i,i).equals(symbol)) return false;
        }
        return true;
    }
    private boolean checkRightDiagonal(int row,int col,String symbol){
        if(row != board.getBoard().length-col-1) return false;
        for(int i=0;i<board.getBoard().length;i++){
            if(!board.getposition(i,board.getBoard().length-i-1).equals(symbol)) return false;
        }
        return true;
    }

}
