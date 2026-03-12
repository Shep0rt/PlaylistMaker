package com.practicum.playlistmaker.presentation.models

data class PlaylistUiDto(
    val id: Long,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackCount: Int
)