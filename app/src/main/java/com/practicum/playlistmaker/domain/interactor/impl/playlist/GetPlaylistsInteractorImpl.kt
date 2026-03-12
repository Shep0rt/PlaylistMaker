package com.practicum.playlistmaker.domain.interactor.impl.playlist

import com.practicum.playlistmaker.domain.interactor.playlist.GetPlaylistsInteractor
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class GetPlaylistsInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : GetPlaylistsInteractor {

    override fun execute(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }
}