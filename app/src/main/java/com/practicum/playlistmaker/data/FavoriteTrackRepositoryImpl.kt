package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.db.mappers.TrackDbMapper
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.FavoriteTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbMapper: TrackDbMapper
) : FavoriteTrackRepository {

    override suspend fun addTrackToFavorite(track: Track) {
        val entity = trackDbMapper.map(track)
        appDatabase.favoriteTrackDao().insertFavoriteTrack(entity)
    }

    override suspend fun deleteTrackFromFavorite(track: Track) {
        val entity = trackDbMapper.map(track)
        appDatabase.favoriteTrackDao().deleteFavoriteTrack(entity)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.favoriteTrackDao().getFavoriteTracks().map { entities ->
            entities.map { trackDbMapper.map(it) }
        }
    }

    override fun getFavoriteTrackIds(): Flow<List<Long>> {
        return appDatabase.favoriteTrackDao().getFavoriteTracks().map { entities ->
            entities.map { it.trackId }
        }
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return appDatabase.favoriteTrackDao().getFavoriteTrackIds().contains(trackId)
    }
}
