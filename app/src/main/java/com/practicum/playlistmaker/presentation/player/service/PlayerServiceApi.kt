package com.practicum.playlistmaker.presentation.player.service

import com.practicum.playlistmaker.presentation.player.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface PlayerServiceApi {
    fun play()
    fun pause()

    fun getPlayerState(): PlayerState
    fun playbackState(): StateFlow<PlayerPlaybackState>

    fun startForegroundMode()
    fun stopForegroundMode()
}

