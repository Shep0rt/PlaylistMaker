package com.practicum.playlistmaker.presentation.player

sealed class AddToPlaylistUiState {
    data class Added(val playlistName: String) : AddToPlaylistUiState()
    data class AlreadyExists(val playlistName: String) : AddToPlaylistUiState()
}