package com.practicum.playlistmaker.domain.interactor.impl.history

import com.practicum.playlistmaker.domain.interactor.history.SaveToHistoryInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.HistoryRepository

class SaveToHistoryInteractorImpl(private val repo: HistoryRepository) : SaveToHistoryInteractor {
    override fun execute(track: Track) = repo.addToHistory(track)
}