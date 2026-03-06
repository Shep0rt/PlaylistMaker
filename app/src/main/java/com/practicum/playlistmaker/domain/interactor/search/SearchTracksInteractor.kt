package com.practicum.playlistmaker.domain.interactor.search

import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchTracksInteractor {
    fun execute(query: String): Flow<Result<List<Track>>>
}