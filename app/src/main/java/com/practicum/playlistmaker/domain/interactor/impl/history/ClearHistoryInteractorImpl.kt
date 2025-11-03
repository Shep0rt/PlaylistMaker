package com.practicum.playlistmaker.domain.interactor.impl.history

import com.practicum.playlistmaker.domain.interactor.history.ClearHistoryInteractor
import com.practicum.playlistmaker.domain.repository.TrackRepository

class ClearHistoryInteractorImpl(private val repo: TrackRepository): ClearHistoryInteractor {
    override fun execute() = repo.clearHistory()
}