package com.practicum.playlistmaker.presentation.player.service

import com.practicum.playlistmaker.presentation.player.PlayerConstants
import com.practicum.playlistmaker.presentation.player.PlayerState

data class PlayerPlaybackState(
    val playerState: PlayerState = PlayerState.Default,
    val progress: String = PlayerConstants.DEFAULT_POSITION_TRACK
)

