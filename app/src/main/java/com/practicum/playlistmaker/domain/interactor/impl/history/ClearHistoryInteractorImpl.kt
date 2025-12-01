package com.practicum.playlistmaker.domain.interactor.impl.history

import com.practicum.playlistmaker.domain.interactor.history.ClearHistoryInteractor
import com.practicum.playlistmaker.domain.repository.HistoryRepository

class ClearHistoryInteractorImpl(private val repo: HistoryRepository): ClearHistoryInteractor {
    override fun execute() = repo.clearHistory()
}