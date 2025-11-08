package com.practicum.playlistmaker.domain.interactor.impl.player

import com.practicum.playlistmaker.domain.interactor.player.PauseTrackInteractor
import com.practicum.playlistmaker.domain.repository.PlayerRepository

class PauseTrackInteractorImpl(private val repo: PlayerRepository) : PauseTrackInteractor {
    override fun execute() {
        repo.pause()
    }
}