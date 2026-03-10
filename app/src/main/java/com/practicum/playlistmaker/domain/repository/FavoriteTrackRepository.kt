package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackRepository {

    suspend fun addTrackToFavorite(track: Track)

    suspend fun deleteTrackFromFavorite(track: Track)

    fun getFavoriteTracks(): Flow<List<Track>>

    fun getFavoriteTrackIds(): Flow<List<Long>>

    suspend fun isFavorite(trackId: Long): Boolean
}
