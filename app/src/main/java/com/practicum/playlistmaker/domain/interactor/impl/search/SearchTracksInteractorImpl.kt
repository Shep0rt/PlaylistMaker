package com.practicum.playlistmaker.domain.interactor.impl.search

import com.practicum.playlistmaker.domain.interactor.search.SearchTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.FavoriteTrackRepository
import com.practicum.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SearchTracksInteractorImpl(
    private val repo: TrackRepository,
    private val favoriteRepository: FavoriteTrackRepository
) : SearchTracksInteractor {

    override fun execute(query: String): Flow<Result<List<Track>>> {
        return repo.search(query).combine(favoriteRepository.getFavoriteTrackIds()) { result, favoriteIds ->
            result.map { tracks ->
                tracks.map { track ->
                    track.copy(isFavorite = favoriteIds.contains(track.id))
                }
            }
        }
    }
}