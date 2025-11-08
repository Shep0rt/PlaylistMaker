package com.practicum.playlistmaker.domain.interactor.impl.player

import com.practicum.playlistmaker.domain.interactor.player.SetPlayerEventsInteractor
import com.practicum.playlistmaker.domain.repository.PlayerEvents
import com.practicum.playlistmaker.domain.repository.PlayerRepository

class SetPlayerEventsInteractorImpl(private val repo: PlayerRepository) : SetPlayerEventsInteractor {
    override fun execute(listener: PlayerEvents?) {
       return repo.setEventsListener(listener)
    }
}