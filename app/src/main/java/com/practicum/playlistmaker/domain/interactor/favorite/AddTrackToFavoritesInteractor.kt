package com.practicum.playlistmaker.domain.interactor.favorite

import com.practicum.playlistmaker.domain.models.Track

interface AddTrackToFavoritesInteractor {
    suspend fun execute(track: Track)
}