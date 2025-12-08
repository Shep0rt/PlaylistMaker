package com.practicum.playlistmaker.domain.interactor.impl.search

import com.practicum.playlistmaker.domain.interactor.search.SearchTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.TrackRepository

class SearchTracksInteractorImpl(private val repo: TrackRepository) : SearchTracksInteractor {
    override fun execute(query: String, callback: (Result<List<Track>>) -> Unit) {
        repo.search(query, callback)
    }
}