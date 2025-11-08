package com.practicum.playlistmaker.domain.interactor.impl.player

import com.practicum.playlistmaker.domain.interactor.player.PlayTrackInteractor
import com.practicum.playlistmaker.domain.repository.PlayerRepository

class PlayTrackInteractorImpl(private val repo: PlayerRepository) : PlayTrackInteractor {
    override fun execute() {
        repo.play()
    }
}