package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.common.PlayerState

interface PlayerRepository {
    fun prepare(url: String): Boolean
    fun play()
    fun pause()
    fun stop()
    fun state(): PlayerState
    fun currentPositionMs(): Int
    fun durationMs(): Int
    fun setEventsListener(listener: PlayerEvents?)
}