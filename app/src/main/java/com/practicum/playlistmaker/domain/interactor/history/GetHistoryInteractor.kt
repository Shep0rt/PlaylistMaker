package com.practicum.playlistmaker.domain.interactor.history

import com.practicum.playlistmaker.domain.models.Track

interface GetHistoryInteractor {
    fun execute(): List<Track>
}