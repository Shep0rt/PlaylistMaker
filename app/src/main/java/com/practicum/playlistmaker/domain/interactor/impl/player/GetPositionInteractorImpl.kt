package com.practicum.playlistmaker.domain.interactor.impl.player

import com.practicum.playlistmaker.domain.interactor.player.GetPositionInteractor
import com.practicum.playlistmaker.domain.repository.PlayerRepository

class GetPositionInteractorImpl(private val repo: PlayerRepository) : GetPositionInteractor {
    override fun execute(): Int {
        return repo.currentPositionMs()
    }
}