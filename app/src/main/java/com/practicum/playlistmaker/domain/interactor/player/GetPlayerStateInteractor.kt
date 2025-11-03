package com.practicum.playlistmaker.domain.interactor.player

import com.practicum.playlistmaker.domain.common.PlayerState

interface GetPlayerStateInteractor {
    fun execute(): PlayerState
}