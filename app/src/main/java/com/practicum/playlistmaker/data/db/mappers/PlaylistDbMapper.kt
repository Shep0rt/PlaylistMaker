package com.practicum.playlistmaker.data.db.mappers

import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistWithTrackCount
import com.practicum.playlistmaker.domain.models.Playlist

class PlaylistDbMapper {

    fun mapToEntity(
        name: String,
        description: String?,
        coverPath: String?,
        createdAt: Long
    ): PlaylistEntity {
        return PlaylistEntity(
            name = name,
            description = description,
            coverPath = coverPath,
            createdAt = createdAt
        )
    }

    fun mapToDomain(playlistWithCount: PlaylistWithTrackCount): Playlist {
        return Playlist(
            id = playlistWithCount.playlist.id,
            name = playlistWithCount.playlist.name,
            description = playlistWithCount.playlist.description,
            coverPath = playlistWithCount.playlist.coverPath,
            trackCount = playlistWithCount.trackCount
        )
    }
}