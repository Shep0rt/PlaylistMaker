package com.practicum.playlistmaker.data.network

data class TrackListResponseDto(
    val resultCount: Int,
    val results: List<TrackResponseDto>
)