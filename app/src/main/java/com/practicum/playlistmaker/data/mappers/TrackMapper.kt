package com.practicum.playlistmaker.data.mappers

import com.practicum.playlistmaker.data.network.TrackResponseDto
import com.practicum.playlistmaker.model.Track

object TrackMapper {

    fun map(track: TrackResponseDto): Track {
        return Track(trackName = track.trackName.orEmpty(),
            artistName = track.artistName.orEmpty(),
            artworkUrl100 = track.artworkUrl100.orEmpty(),
            trackTime = formatMs(track.trackTimeMillis ?: 0L))
    }

    fun mapList(tracksResponse: List<TrackResponseDto>): List<Track> {
        val result = mutableListOf<Track>()
        for (trackResponse in tracksResponse) {
            result.add(map(trackResponse))
        }
        return result
    }

    fun formatMs(ms: Long): String {
        val totalSec = ms / 1000
        val mm = totalSec / 60
        val ss = totalSec % 60
        return "%d:%02d".format(mm, ss)
    }
}