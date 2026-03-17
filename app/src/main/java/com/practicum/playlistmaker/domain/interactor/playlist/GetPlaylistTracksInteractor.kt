package com.practicum.playlistmaker.domain.interactor.playlist

import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface GetPlaylistTracksInteractor {
    fun execute(playlistId: Long): Flow<List<Track>>
}
