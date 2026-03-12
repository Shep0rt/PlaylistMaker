package com.practicum.playlistmaker.presentation.media.playlist

import com.practicum.playlistmaker.presentation.models.PlaylistUiDto

sealed class PlaylistsState {
    object Loading : PlaylistsState()
    object Empty : PlaylistsState()
    data class Content(val playlists: List<PlaylistUiDto>) : PlaylistsState()
}