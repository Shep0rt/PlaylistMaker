package com.practicum.playlistmaker.domain.interactor.impl.player

import com.practicum.playlistmaker.domain.interactor.player.GetPlayerStateInteractor
import com.practicum.playlistmaker.domain.common.PlayerState
import com.practicum.playlistmaker.domain.repository.PlayerRepository

class GetPlayerStateInteractorImpl(private val repo: PlayerRepository) : GetPlayerStateInteractor {
    override fun execute(): PlayerState {
        return repo.state()
    }
}