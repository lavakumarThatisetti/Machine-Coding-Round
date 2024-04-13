package com.lavakumar.snakeandladderoptimal.model;

public class Player implements Cloneable {
    int playerId;
    String playerName;
    boolean isWin;
    Integer playerPosition;
    Integer totalMoves;

    public Player(int playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.isWin = false;
        this.playerPosition = 0;
        this.totalMoves = 0;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
    }

    public Integer getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(Integer playerPosition) {
        this.playerPosition = playerPosition;
    }

    public Integer getTotalMoves() {
        return totalMoves;
    }

    public void setTotalMoves(Integer totalMoves) {
        this.totalMoves = totalMoves;
    }


    @Override
    public Player clone() {
        try {
            Player clone = (Player) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
