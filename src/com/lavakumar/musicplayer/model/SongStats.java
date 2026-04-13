package com.lavakumar.musicplayer.model;

public record SongStats(
        String songId,
        String title,
        String artist,
        long uniqueUserCount,
        long totalPlayCount
) {}
