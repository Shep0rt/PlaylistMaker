package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.data.db.dao.TrackDao
import com.practicum.playlistmaker.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.data.db.mappers.TrackDbMapper
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.FavoriteTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class FavoriteTrackRepositoryImpl(
    private val favoriteTrackDao: FavoriteTrackDao,
    private val trackDao: TrackDao,
    private val trackDbMapper: TrackDbMapper
) : FavoriteTrackRepository {

    override suspend fun addTrackToFavorite(track: Track) {
        trackDao.insertTrack(trackDbMapper.map(track))
        favoriteTrackDao.insertFavoriteTrack(
            FavoriteTrackEntity(
                trackId = track.id,
                addedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun deleteTrackFromFavorite(track: Track) {
        favoriteTrackDao.deleteFavoriteTrack(track.id)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackDao.getFavoriteTracks()
            .distinctUntilChanged()
            .map { entities ->
                entities.map { trackDbMapper.map(it, isFavorite = true) }
        }
    }

    override fun getFavoriteTrackIds(): Flow<List<Long>> {
        return favoriteTrackDao.getFavoriteTrackIds()
            .distinctUntilChanged()
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return favoriteTrackDao.isFavorite(trackId)
    }
}