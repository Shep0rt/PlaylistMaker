package com.practicum.playlistmaker.domain.interactor.impl.playlist

import com.practicum.playlistmaker.domain.interactor.playlist.RemoveTrackFromPlaylistInteractor
import com.practicum.playlistmaker.domain.repository.PlaylistRepository

class RemoveTrackFromPlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : RemoveTrackFromPlaylistInteractor {

    override suspend fun execute(playlistId: Long, trackId: Long) {
        playlistRepository.removeTrackFromPlaylist(playlistId, trackId)
    }
}
