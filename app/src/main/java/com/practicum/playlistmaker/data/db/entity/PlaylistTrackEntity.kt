package com.practicum.playlistmaker.data.db.entity

import androidx.room.Entity

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlistId", "trackId"]
)
data class PlaylistTrackEntity(
    val playlistId: Long,
    val trackId: Long,
    val addedAt: Long
)