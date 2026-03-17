package com.practicum.playlistmaker.domain.interactor.impl.playlist

import com.practicum.playlistmaker.domain.interactor.playlist.RemovePlaylistInteractor
import com.practicum.playlistmaker.domain.repository.PlaylistRepository

class RemovePlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : RemovePlaylistInteractor {

    override suspend fun execute(playlistId: Long) {
        playlistRepository.removePlaylist(playlistId)
    }
}
