package com.practicum.playlistmaker.presentation.search

import com.practicum.playlistmaker.presentation.models.TrackUiDto

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Content(val tracks: List<TrackUiDto>) : SearchState()
    object Empty : SearchState()
    object Error : SearchState()
    data class History(val tracks: List<TrackUiDto>) : SearchState()
}