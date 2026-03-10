package com.practicum.playlistmaker.domain.interactor.impl.favorite

import com.practicum.playlistmaker.domain.interactor.favorite.IsFavoriteTrackInteractor
import com.practicum.playlistmaker.domain.repository.FavoriteTrackRepository

class IsFavoriteTrackInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
) : IsFavoriteTrackInteractor {
    override suspend fun execute(trackId: Long): Boolean {
        return favoriteTrackRepository.isFavorite(trackId)
    }
}