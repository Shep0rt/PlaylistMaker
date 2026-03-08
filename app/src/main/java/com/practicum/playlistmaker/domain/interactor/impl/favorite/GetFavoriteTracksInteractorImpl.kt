package com.practicum.playlistmaker.domain.interactor.impl.favorite

import com.practicum.playlistmaker.domain.interactor.favorite.GetFavoriteTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.FavoriteTrackRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteTracksInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
) : GetFavoriteTracksInteractor {
    override fun execute(): Flow<List<Track>> {
        return favoriteTrackRepository.getFavoriteTracks()
    }
}