package com.practicum.playlistmaker.domain.interactor.player

import com.practicum.playlistmaker.domain.repository.PlayerEvents

interface SetPlayerEventsInteractor {
    fun execute(listener: PlayerEvents?)
}