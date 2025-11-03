package com.practicum.playlistmaker.domain.interactor.impl.search

import com.practicum.playlistmaker.domain.common.DataResult
import com.practicum.playlistmaker.domain.common.ErrorType
import com.practicum.playlistmaker.domain.interactor.search.SearchTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.TrackRepository

class SearchTracksInteractorImpl(private val repo: TrackRepository) : SearchTracksInteractor {
    override fun execute(query: String, callback: (DataResult<List<Track>>) -> Unit) {
        repo.search(query) { result ->
            if (result.isSuccess) {
                val items = result.data.orEmpty()
                if (items.isEmpty()) {
                    callback(DataResult(isSuccess = false, error = ErrorType.EMPTY))
                } else {
                    callback(DataResult(isSuccess = true, data = items))
                }
            } else {
                callback(result)
            }
        }
    }
}