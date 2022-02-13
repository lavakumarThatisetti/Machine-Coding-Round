package com.lavakumar.bowlingalley;

import com.lavakumar.bowlingalley.model.Player;
import com.lavakumar.bowlingalley.scoreboard.ScoreBoardImpl;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Player p1 = new Player(1,"Player1", new ScoreBoardImpl());
        Player p2 = new Player(2, "Player2", new ScoreBoardImpl());

        List<Player> players = Arrays.asList(p1,p2);
        BowlingGameService bowlingGameService = new BowlingGameService(players);

        bowlingGameService.startGame();

        System.out.println(bowlingGameService.getWinner());
    }
}
