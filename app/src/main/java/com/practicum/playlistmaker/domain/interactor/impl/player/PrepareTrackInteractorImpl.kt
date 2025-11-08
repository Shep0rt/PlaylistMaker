package com.practicum.playlistmaker.domain.interactor.impl.player

import com.practicum.playlistmaker.domain.interactor.player.PrepareTrackInteractor
import com.practicum.playlistmaker.domain.repository.PlayerRepository

class PrepareTrackInteractorImpl(private val repo: PlayerRepository) : PrepareTrackInteractor {
    override fun execute(url: String): Boolean {
        return repo.prepare(url)
    }
}