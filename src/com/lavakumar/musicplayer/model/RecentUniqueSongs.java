package com.lavakumar.musicplayer.model;

import java.util.*;

public class RecentUniqueSongs {
    private final int limit;
    private final Deque<String> songsMostRecentFirst = new ArrayDeque<>();
    private final Set<String> present = new HashSet<>();

    public RecentUniqueSongs(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be > 0");
        }
        this.limit = limit;
    }

    public void recordSong(String songId) {
        if (present.contains(songId)) {
            songsMostRecentFirst.remove(songId);
            songsMostRecentFirst.addFirst(songId);
            return;
        }

        songsMostRecentFirst.addFirst(songId);
        present.add(songId);

        if (songsMostRecentFirst.size() > limit) {
            String removed = songsMostRecentFirst.removeLast();
            present.remove(removed);
        }
    }

    public List<String> snapshotSongIdsMostRecentFirst() {
        return new ArrayList<>(songsMostRecentFirst);
    }
}
