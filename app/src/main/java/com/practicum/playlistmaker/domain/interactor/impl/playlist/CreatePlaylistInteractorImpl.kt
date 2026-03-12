package com.practicum.playlistmaker.domain.interactor.impl.playlist

import com.practicum.playlistmaker.domain.interactor.playlist.CreatePlaylistInteractor
import com.practicum.playlistmaker.domain.repository.PlaylistRepository

class CreatePlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : CreatePlaylistInteractor {

    override suspend fun execute(
        name: String,
        description: String?,
        coverPath: String?
    ): Long {
        return playlistRepository.createPlaylist(name, description, coverPath)
    }
}