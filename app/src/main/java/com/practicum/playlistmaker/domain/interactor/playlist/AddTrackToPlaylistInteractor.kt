package com.practicum.playlistmaker.domain.interactor.playlist

import com.practicum.playlistmaker.domain.interactor.impl.playlist.AddToPlaylistResult
import com.practicum.playlistmaker.domain.models.Track

interface AddTrackToPlaylistInteractor {
    suspend fun execute(track: Track, playlistId: Long): AddToPlaylistResult
}