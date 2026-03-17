package com.practicum.playlistmaker.domain.interactor.impl.playlist

import com.practicum.playlistmaker.domain.interactor.playlist.GetPlaylistInteractor
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class GetPlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : GetPlaylistInteractor {

    override fun execute(playlistId: Long): Flow<Playlist?> {
        return playlistRepository.getPlaylist(playlistId)
    }
}
