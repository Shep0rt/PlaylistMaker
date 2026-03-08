package com.practicum.playlistmaker.domain.interactor.impl.favorite

import com.practicum.playlistmaker.domain.interactor.favorite.RemoveTrackFromFavoritesInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.FavoriteTrackRepository

class RemoveTrackFromFavoritesInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
) : RemoveTrackFromFavoritesInteractor {
    override suspend fun execute(track: Track) {
        favoriteTrackRepository.deleteTrackFromFavorite(track)
    }
}