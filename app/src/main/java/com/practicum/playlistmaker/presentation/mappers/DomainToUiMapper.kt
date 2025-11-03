package com.practicum.playlistmaker.presentation.mappers

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.models.TrackUiDto

object DomainToUiMapper {

    fun trackToUi(track: Track): TrackUiDto {
        return TrackUiDto(
            id = track.id,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = formatDuration(track.trackTime),
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun listToUi(list: List<Track>): List<TrackUiDto>{
        return list.map(::trackToUi)
    }

    private fun formatDuration(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}