package com.practicum.playlistmaker.domain.interactor.playlist

import com.practicum.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface GetPlaylistsInteractor {
    fun execute(): Flow<List<Playlist>>
}