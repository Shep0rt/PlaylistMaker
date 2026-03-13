package com.practicum.playlistmaker.domain.interactor.playlist

interface RemovePlaylistInteractor {
    suspend fun execute(playlistId: Long)
}
