package com.lavakumar.musicplayer.repository;

import com.lavakumar.musicplayer.model.Song;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SongRepository {
    private final ConcurrentHashMap<String, Song> songsById = new ConcurrentHashMap<>();

    public boolean save(Song song) {
        return songsById.putIfAbsent(song.songId(), song) == null;
    }

    public Optional<Song> findById(String songId) {
        return Optional.ofNullable(songsById.get(songId));
    }

    public Collection<Song> findAll() {
        return songsById.values();
    }
}
