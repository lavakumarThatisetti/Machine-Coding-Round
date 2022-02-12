package com.lavakumar.tictactoe.service;


import com.lavakumar.tictactoe.exceptions.BoardException;
import com.lavakumar.tictactoe.mode.Print;
import com.lavakumar.tictactoe.model.Board;
import com.lavakumar.tictactoe.model.Player;
import com.lavakumar.tictactoe.validations.GameValidator;

import java.util.List;

public class TicTacToeService {
    Board board;
    List<Player> players;
    GameValidator gameValidator;
    Print print;
    int gameCount = 0;


    public TicTacToeService(Board board, List<Player> players, GameValidator gameValidator, Print print){
        this.board = board;
        this.players = players;
        this.gameValidator = gameValidator;
        this.print = print;
    }

    public void input(int x, int y, Player player){
         if(!gameValidator.validateCoordinates(x,y)){
             throw new BoardException("Inputs are Not Valid");
         }
         if(!gameValidator.validateIsBoardPositionEmpty(x,y)){
             throw new BoardException("Board point Already Filled");
         }
         String symbol = player.getSymbol();
         board.setposition(x,y,symbol);
         print.printBoard(board);
         if(checkBoard(x,y,symbol)){
             System.out.println(" Player "+player.getPlayerName()+" Wins");
             System.exit(0);
         }
         gameCount++;
         if(checkBoardFill()){
             System.out.println("Game Draw");
             System.exit(0);
         }
    }

    private boolean checkBoard(int row, int column, String symbol){
        boolean winRow = true, winColm = true, winLeft = true, winRight = true;
        for(int i=0;i<board.getBoard().length;i++){
            if(!board.getposition(row,i).equals(symbol)){
                winRow = false;
            }
            if(!board.getposition(i,column).equals(symbol)){
                winColm = false;
            }
            if(!board.getposition(i,i).equals(symbol)){
                winLeft = false;
            }
            if(!board.getposition(i,board.getBoard().length-1-i).equals(symbol)){
                winRight = false;
            }
        }
        return winRow || winColm || winLeft || winRight;
    }

    private boolean checkBoardFill(){
        return gameCount == board.getBoard().length * board.getBoard()[0].length;
    }
}
