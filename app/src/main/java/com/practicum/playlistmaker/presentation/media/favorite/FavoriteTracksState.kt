package com.practicum.playlistmaker.presentation.media.favorite

import com.practicum.playlistmaker.presentation.models.TrackUiDto

sealed interface FavoriteTracksState {
    object Loading : FavoriteTracksState
    object Empty : FavoriteTracksState
    data class Content(val tracks: List<TrackUiDto>) : FavoriteTracksState
}