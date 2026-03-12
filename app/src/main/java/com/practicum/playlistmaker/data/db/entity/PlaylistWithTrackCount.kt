package com.practicum.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class PlaylistWithTrackCount(
    @Embedded val playlist: PlaylistEntity,
    @ColumnInfo(name = "trackCount") val trackCount: Int
)