package com.practicum.playlistmaker.presentation.models

import com.practicum.playlistmaker.presentation.player.PlayerConstants
import com.practicum.playlistmaker.presentation.player.PlayerState

data class PlayerUiState(
    val playerState: PlayerState = PlayerState.Default,
    val progress: String = PlayerConstants.DEFAULT_POSITION_TRACK
)