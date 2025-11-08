package com.practicum.playlistmaker.domain.interactor.player

interface PrepareTrackInteractor {
    fun execute(url: String): Boolean
}