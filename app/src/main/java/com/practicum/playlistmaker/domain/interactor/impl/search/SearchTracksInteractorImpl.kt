package com.practicum.playlistmaker.domain.interactor.impl.search

import com.practicum.playlistmaker.domain.interactor.search.SearchTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow

class SearchTracksInteractorImpl(private val repo: TrackRepository) : SearchTracksInteractor {
    override fun execute(query: String): Flow<Result<List<Track>>> {
        return repo.search(query)
    }
}