package com.practicum.playlistmaker.domain.interactor.impl.playlist

import com.practicum.playlistmaker.domain.interactor.playlist.UpdatePlaylistInteractor
import com.practicum.playlistmaker.domain.repository.PlaylistRepository

class UpdatePlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : UpdatePlaylistInteractor {

    override suspend fun execute(
        playlistId: Long,
        name: String,
        description: String?,
        coverPath: String?
    ) {
        playlistRepository.updatePlaylist(
            playlistId = playlistId,
            name = name,
            description = description,
            coverPath = coverPath
        )
    }
}
