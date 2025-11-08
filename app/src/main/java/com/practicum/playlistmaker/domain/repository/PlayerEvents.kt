package com.practicum.playlistmaker.domain.repository

interface PlayerEvents {
    fun onPrepared()
    fun onCompleted()
}