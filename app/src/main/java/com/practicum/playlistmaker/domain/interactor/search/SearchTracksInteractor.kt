package com.practicum.playlistmaker.domain.interactor.search

import com.practicum.playlistmaker.domain.common.DataResult
import com.practicum.playlistmaker.domain.models.Track

interface SearchTracksInteractor {
    fun execute(query: String, callback: (DataResult<List<Track>>) -> Unit)
}