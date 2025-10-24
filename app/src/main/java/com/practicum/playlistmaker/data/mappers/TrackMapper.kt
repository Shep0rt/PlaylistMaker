package com.practicum.playlistmaker.data.mappers

import com.practicum.playlistmaker.data.network.TrackResponseDto
import com.practicum.playlistmaker.model.Track

object TrackMapper {

    fun map(track: TrackResponseDto): Track {
        return Track(
            id = track.id ?: 0L,
            trackName = track.trackName.orEmpty(),
            artistName = track.artistName.orEmpty(),
            trackTime = track.trackTimeMillis ?: 0L,
            artworkUrl100 = track.artworkUrl100.orEmpty()
        )
    }

    fun mapList(tracksResponse: List<TrackResponseDto>): List<Track> {
        val result = mutableListOf<Track>()
        for (trackResponse in tracksResponse) {
            result.add(map(trackResponse))
        }
        return result
    }
}