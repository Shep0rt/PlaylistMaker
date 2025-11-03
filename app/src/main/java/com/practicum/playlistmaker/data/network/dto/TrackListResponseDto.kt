package com.practicum.playlistmaker.data.network.dto

data class TrackListResponseDto(val resultCount: Int,
                                val results: List<TrackResponseDto>)