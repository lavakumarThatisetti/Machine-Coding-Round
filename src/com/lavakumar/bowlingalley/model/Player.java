package com.lavakumar.bowlingalley.model;

import com.lavakumar.bowlingalley.scoreboard.ScoreBoard;

public class Player {
    private int id;
    private String playerName;
    private final ScoreBoard scoreBoard;
    private boolean isWin;

    public Player(int id, String playerName, ScoreBoard scoreBoard){
        this.id = id;
        this.playerName = playerName;
        this.isWin = false;
        this.scoreBoard = scoreBoard;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWin(boolean win) {
        isWin = win;
    }
    public boolean getWin() {
       return isWin;
    }

    public ScoreBoard getScoreBoard(){
        return scoreBoard;
    }


    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", playerName='" + playerName + '\'' +
                ", score=" + scoreBoard.score()+
                ", isWin=" + isWin +
                '}';
    }
}
