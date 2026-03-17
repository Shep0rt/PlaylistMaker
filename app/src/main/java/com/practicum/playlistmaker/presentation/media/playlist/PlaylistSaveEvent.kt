package com.practicum.playlistmaker.presentation.media.playlist

sealed interface PlaylistSaveEvent {
    data class Created(val name: String) : PlaylistSaveEvent
    data object Updated : PlaylistSaveEvent
}
