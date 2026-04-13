package com.lavakumar.musicplayer.model;

public record SongRanking(
        String songId,
        String title,
        String artist,
        long uniqueUserCount,
        long totalPlayCount
) {}
