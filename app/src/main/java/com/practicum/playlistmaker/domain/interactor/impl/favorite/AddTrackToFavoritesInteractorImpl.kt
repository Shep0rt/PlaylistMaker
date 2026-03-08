package com.practicum.playlistmaker.domain.interactor.impl.favorite

import com.practicum.playlistmaker.domain.interactor.favorite.AddTrackToFavoritesInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.FavoriteTrackRepository

class AddTrackToFavoritesInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
) : AddTrackToFavoritesInteractor {
    override suspend fun execute(track: Track) {
        favoriteTrackRepository.addTrackToFavorite(track)
    }
}