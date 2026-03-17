package com.practicum.playlistmaker.domain.interactor.playlist

interface UpdatePlaylistInteractor {
    suspend fun execute(
        playlistId: Long,
        name: String,
        description: String?,
        coverPath: String?
    )
}
