package com.lavakumar.tictactoe.model;

public class Board {
    String[][] board;

    public Board(int N){
        board = new String[N][N];
        for (String[] strings : board) {
            for (int j = 0; j < board.length; j++) {
                strings[j] = "";
            }
        }
    }

    public void printBoard(){
        for (String[] strings : board) {
            for (int j = 0; j < board.length; j++) {
                System.out.print(strings[j] + " ");
            }
            System.out.println();
        }
    }
    public void setposition(int x, int j,String symbol){
        board[x][j] = symbol;
    }
    public String getposition(int x, int j){
        return board[x][j];
    }

    public String[][] getBoard() {
        return board;
    }
}
