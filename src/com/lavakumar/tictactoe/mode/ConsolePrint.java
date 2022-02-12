package com.lavakumar.tictactoe.mode;


import com.lavakumar.tictactoe.model.Board;

public class ConsolePrint implements Print {
    @Override
    public void printBoard(Board board) {
        for(String[] strs: board.getBoard()){
            for(int j=0;j<board.getBoard().length;j++){
                System.out.print(strs[j]+" ");
            }
            System.out.println();
        }
    }
}
