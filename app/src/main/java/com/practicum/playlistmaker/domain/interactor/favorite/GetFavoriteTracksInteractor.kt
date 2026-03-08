package com.practicum.playlistmaker.domain.interactor.favorite

import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface GetFavoriteTracksInteractor {
    fun execute(): Flow<List<Track>>
}