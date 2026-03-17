package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.interactor.impl.playlist.AddToPlaylistResult
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverPath: String?
    ): Long

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(track: Track, playlistId: Long): AddToPlaylistResult

    fun getPlaylist(playlistId: Long): Flow<Playlist?>

    fun getPlaylistTracks(playlistId: Long): Flow<List<Track>>

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    suspend fun removePlaylist(playlistId: Long)

    suspend fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String?,
        coverPath: String?
    )
}
