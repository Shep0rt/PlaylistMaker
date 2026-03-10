package com.practicum.playlistmaker.domain.interactor.favorite

import kotlinx.coroutines.flow.Flow

interface GetFavoriteTrackIdsInteractor {
    fun execute(): Flow<List<Long>>
}
