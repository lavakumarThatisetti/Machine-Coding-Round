package com.lavakumar.musicplayer;

import com.lavakumar.musicplayer.model.*;
import com.lavakumar.musicplayer.repository.SongRepository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MusicPlayerService {
    private final SongRepository songRepository;
    private final int recentUniqueLimit;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    // songId -> users who have ever played the song
    private final ConcurrentHashMap<String, Set<String>> uniqueUsersBySong = new ConcurrentHashMap<>();

    // songId -> total play count
    private final ConcurrentHashMap<String, Long> totalPlaysBySong = new ConcurrentHashMap<>();

    // userId -> last N unique songs
    private final ConcurrentHashMap<String, RecentUniqueSongs> recentUniqueSongsByUser = new ConcurrentHashMap<>();

    // optional: sequence number for play ordering / future audit
    private final AtomicLong playSequenceGenerator = new AtomicLong(0);

    MusicPlayerService(SongRepository songRepository, int recentUniqueLimit) {
        this.songRepository = Objects.requireNonNull(songRepository);
        if (recentUniqueLimit <= 0) {
            throw new IllegalArgumentException("recentUniqueLimit must be > 0");
        }
        this.recentUniqueLimit = recentUniqueLimit;
    }

    public void addSong(String songId, String title, String artist) {
        validateSongId(songId);
        validateTitle(title);
        validateArtist(artist);

        lock.writeLock().lock();
        try {
            Song song = new Song(songId.trim(), title.trim(), artist.trim());
            boolean inserted = songRepository.save(song);
            if (!inserted) {
                throw new IllegalArgumentException("Song already exists: " + songId);
            }

            uniqueUsersBySong.putIfAbsent(songId, ConcurrentHashMap.newKeySet());
            totalPlaysBySong.putIfAbsent(songId, 0L);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public PlayResult playSong(String userId, String songId) {
        validateUserId(userId);
        validateSongId(songId);

        lock.writeLock().lock();
        try {
            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown song: " + songId));

            long playSequence = playSequenceGenerator.incrementAndGet();

            Set<String> uniqueUsers = uniqueUsersBySong.computeIfAbsent(songId, ignored -> ConcurrentHashMap.newKeySet());
            boolean isFirstPlayByUserForSong = uniqueUsers.add(userId);

            totalPlaysBySong.merge(songId, 1L, Long::sum);

            RecentUniqueSongs recentUniqueSongs = recentUniqueSongsByUser.computeIfAbsent(
                    userId,
                    ignored -> new RecentUniqueSongs(recentUniqueLimit)
            );
            recentUniqueSongs.recordSong(songId);

            long uniqueUserCount = uniqueUsers.size();
            long totalPlayCount = totalPlaysBySong.getOrDefault(songId, 0L);

            return new PlayResult(
                    playSequence,
                    userId,
                    song.songId(),
                    song.title(),
                    song.artist(),
                    isFirstPlayByUserForSong,
                    uniqueUserCount,
                    totalPlayCount,
                    Instant.now()
            );
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<SongRanking> getSongsRankedByUniqueUsers() {
        lock.readLock().lock();
        try {
            List<SongRanking> result = new ArrayList<>();

            for (Song song : songRepository.findAll()) {
                long uniqueUserCount = uniqueUsersBySong.getOrDefault(song.songId(), Set.of()).size();
                long totalPlayCount = totalPlaysBySong.getOrDefault(song.songId(), 0L);

                result.add(new SongRanking(
                        song.songId(),
                        song.title(),
                        song.artist(),
                        uniqueUserCount,
                        totalPlayCount
                ));
            }

            result.sort(
                    Comparator.comparingLong(SongRanking::uniqueUserCount).reversed()
                            .thenComparingLong(SongRanking::totalPlayCount).reversed()
                            .thenComparing(SongRanking::songId)
            );

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<RecentSongView> getLastUniqueSongsForUser(String userId) {
        validateUserId(userId);

        lock.readLock().lock();
        try {
            RecentUniqueSongs recentUniqueSongs = recentUniqueSongsByUser.get(userId);
            if (recentUniqueSongs == null) {
                return List.of();
            }

            List<String> songIds = recentUniqueSongs.snapshotSongIdsMostRecentFirst();
            List<RecentSongView> result = new ArrayList<>();

            for (String songId : songIds) {
                Song song = songRepository.findById(songId)
                        .orElseThrow(() -> new IllegalStateException("Song missing: " + songId));

                result.add(new RecentSongView(
                        song.songId(),
                        song.title(),
                        song.artist()
                ));
            }

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public SongStats getSongStats(String songId) {
        validateSongId(songId);

        lock.readLock().lock();
        try {
            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown song: " + songId));

            return new SongStats(
                    song.songId(),
                    song.title(),
                    song.artist(),
                    uniqueUsersBySong.getOrDefault(songId, Set.of()).size(),
                    totalPlaysBySong.getOrDefault(songId, 0L)
            );
        } finally {
            lock.readLock().unlock();
        }
    }

    private void validateSongId(String songId) {
        if (songId == null || songId.isBlank()) {
            throw new IllegalArgumentException("songId must not be blank");
        }
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
    }

    private void validateArtist(String artist) {
        if (artist == null || artist.isBlank()) {
            throw new IllegalArgumentException("artist must not be blank");
        }
    }
}
