package com.practicum.playlistmaker.data.db.mappers

import com.practicum.playlistmaker.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.domain.models.Track

class TrackDbMapper {

    fun map(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            trackId = track.id,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            timestamp = System.currentTimeMillis()
        )
    }

    fun map(entity: FavoriteTrackEntity): Track {
        return Track(
            id = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTime = entity.trackTime,
            artworkUrl100 = entity.artworkUrl100,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl,
            isFavorite = true
        )
    }
}