package com.practicum.playlistmaker.presentation.media.playlist

import com.practicum.playlistmaker.presentation.models.PlaylistDetailsUiDto

sealed interface PlaylistUiState {
    data object Loading : PlaylistUiState
    data class Content(val playlist: PlaylistDetailsUiDto) : PlaylistUiState
    data object NotFound : PlaylistUiState
}
