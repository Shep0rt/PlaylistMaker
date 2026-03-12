package com.practicum.playlistmaker.domain.interactor.playlist

interface CreatePlaylistInteractor {
    suspend fun execute(
        name: String,
        description: String?,
        coverPath: String?
    ): Long
}