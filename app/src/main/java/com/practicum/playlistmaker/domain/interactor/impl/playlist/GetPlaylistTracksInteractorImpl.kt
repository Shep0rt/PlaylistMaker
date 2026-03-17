package com.practicum.playlistmaker.domain.interactor.impl.playlist

import com.practicum.playlistmaker.domain.interactor.playlist.GetPlaylistTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class GetPlaylistTracksInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : GetPlaylistTracksInteractor {

    override fun execute(playlistId: Long): Flow<List<Track>> {
        return playlistRepository.getPlaylistTracks(playlistId)
    }
}
