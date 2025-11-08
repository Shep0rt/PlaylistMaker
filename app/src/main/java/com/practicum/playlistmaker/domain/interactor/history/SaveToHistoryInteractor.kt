package com.practicum.playlistmaker.domain.interactor.history

import com.practicum.playlistmaker.domain.models.Track

interface SaveToHistoryInteractor {
    fun execute(track: Track)
}