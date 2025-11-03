package com.practicum.playlistmaker.domain.models

data class Track(
    val id: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
)