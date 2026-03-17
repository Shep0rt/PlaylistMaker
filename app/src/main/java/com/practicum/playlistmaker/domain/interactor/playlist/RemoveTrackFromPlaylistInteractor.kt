package com.practicum.playlistmaker.domain.interactor.playlist

interface RemoveTrackFromPlaylistInteractor {
    suspend fun execute(playlistId: Long, trackId: Long)
}
