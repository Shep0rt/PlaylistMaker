package com.practicum.playlistmaker.domain.interactor.impl.playlist

import com.practicum.playlistmaker.domain.interactor.playlist.AddTrackToPlaylistInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.PlaylistRepository

class AddTrackToPlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : AddTrackToPlaylistInteractor {

    override suspend fun execute(track: Track, playlistId: Long): AddToPlaylistResult {
        return playlistRepository.addTrackToPlaylist(track, playlistId)
    }
}