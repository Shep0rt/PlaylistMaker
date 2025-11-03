package com.practicum.playlistmaker.presentation.mappers

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.models.TrackUiDto

object UiToDomainMapper {

    fun trackToDomain(ui: TrackUiDto): Track {
        return Track(
            id = ui.id,
            trackName = ui.trackName,
            artistName = ui.artistName,
            trackTime = parseDuration(ui.trackTime),
            artworkUrl100 = ui.artworkUrl100 ?: "",
            collectionName = ui.collectionName,
            releaseDate = ui.releaseDate,
            primaryGenreName = ui.primaryGenreName,
            country = ui.country,
            previewUrl = ui.previewUrl
        )
    }

    private fun parseDuration(time: String): Long {
        val parts = time.split(":")
        val mm = parts.getOrNull(0)?.toLongOrNull() ?: 0L
        val ss = parts.getOrNull(1)?.toLongOrNull() ?: 0L
        return (mm * 60 + ss) * 1000L
    }
}