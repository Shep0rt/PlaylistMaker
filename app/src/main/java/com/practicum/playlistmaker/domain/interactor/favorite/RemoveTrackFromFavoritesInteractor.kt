package com.practicum.playlistmaker.domain.interactor.favorite

import com.practicum.playlistmaker.domain.models.Track

interface RemoveTrackFromFavoritesInteractor {
    suspend fun execute(track: Track)
}