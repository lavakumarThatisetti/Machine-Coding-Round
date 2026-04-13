package com.lavakumar.musicplayer;

import com.lavakumar.musicplayer.model.SongRanking;
import com.lavakumar.musicplayer.repository.SongRepository;

public class MusicPlayerDemo {
    public static void main(String[] args) {
        SongRepository songRepository = new SongRepository();
        MusicPlayerService musicPlayerService = new MusicPlayerService(songRepository, 3);

        runDemo(musicPlayerService);
    }

    private static void runDemo(MusicPlayerService service) {
        service.addSong("S1", "Believer", "Imagine Dragons");
        service.addSong("S2", "Naatu Naatu", "MM Keeravani");
        service.addSong("S3", "Shape of You", "Ed Sheeran");
        service.addSong("S4", "Kesariya", "Arijit Singh");

        service.playSong("U1", "S1");
        service.playSong("U1", "S2");
        service.playSong("U1", "S3");

        service.playSong("U2", "S1");
        service.playSong("U2", "S3");
        service.playSong("U2", "S4");

        service.playSong("U3", "S1");
        service.playSong("U3", "S2");

        service.playSong("U1", "S1"); // repeated play by same user, does not increase unique listener count
        service.playSong("U1", "S4"); // now U1's last 3 unique songs should become S4, S1, S3

        System.out.println("========== SONG RANKING BY UNIQUE USERS ==========");
        for (SongRanking ranking : service.getSongsRankedByUniqueUsers()) {
            System.out.println(ranking);
        }

        System.out.println();
        System.out.println("========== LAST 3 UNIQUE SONGS PER USER ==========");
        System.out.println("U1 -> " + service.getLastUniqueSongsForUser("U1"));
        System.out.println("U2 -> " + service.getLastUniqueSongsForUser("U2"));
        System.out.println("U3 -> " + service.getLastUniqueSongsForUser("U3"));

        System.out.println();
        System.out.println("========== SONG DETAILS ==========");
        System.out.println(service.getSongStats("S1"));
        System.out.println(service.getSongStats("S2"));
        System.out.println(service.getSongStats("S3"));
        System.out.println(service.getSongStats("S4"));
    }
}
