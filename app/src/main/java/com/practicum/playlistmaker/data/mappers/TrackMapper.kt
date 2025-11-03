package com.practicum.playlistmaker.data.mappers

import com.practicum.playlistmaker.data.network.dto.TrackResponseDto
import com.practicum.playlistmaker.domain.models.Track

object TrackMapper {

    fun TrackResponseDto.toDomain() = Track(
        id = id ?: 0L,
        trackName = trackName.orEmpty(),
        artistName = artistName.orEmpty(),
        trackTime = trackTimeMillis ?: 0L,
        artworkUrl100 = artworkUrl100.orEmpty(),
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}