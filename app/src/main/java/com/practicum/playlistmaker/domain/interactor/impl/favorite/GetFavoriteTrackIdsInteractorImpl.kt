package com.practicum.playlistmaker.domain.interactor.impl.favorite

import com.practicum.playlistmaker.domain.interactor.favorite.GetFavoriteTrackIdsInteractor
import com.practicum.playlistmaker.domain.repository.FavoriteTrackRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteTrackIdsInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
) : GetFavoriteTrackIdsInteractor {
    override fun execute(): Flow<List<Long>> {
        return favoriteTrackRepository.getFavoriteTrackIds()
    }
}