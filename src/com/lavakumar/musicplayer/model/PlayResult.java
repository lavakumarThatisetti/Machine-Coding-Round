package com.lavakumar.musicplayer.model;

import java.time.Instant;

public record PlayResult(
        long playSequence,
        String userId,
        String songId,
        String title,
        String artist,
        boolean firstTimeUserPlayedThisSong,
        long uniqueUserCountForSong,
        long totalPlayCountForSong,
        Instant playedAt
) {}
