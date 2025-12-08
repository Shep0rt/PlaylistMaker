package com.practicum.playlistmaker.domain.interactor.impl.history

import com.practicum.playlistmaker.domain.interactor.history.GetHistoryInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.HistoryRepository

class GetHistoryInteractorImpl(private val repo: HistoryRepository): GetHistoryInteractor {
    override fun execute(): List<Track> = repo.history()
}