package com.practicum.playlistmaker.presentation.models

data class PlaylistDetailsUiDto(
    val id: Long,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val durationMinutes: Int,
    val trackCount: Int
)
