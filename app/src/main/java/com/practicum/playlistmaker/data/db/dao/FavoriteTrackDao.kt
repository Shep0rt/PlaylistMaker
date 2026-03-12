package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(track: FavoriteTrackEntity)

    @Query("DELETE FROM favorite_tracks WHERE trackId = :trackId")
    suspend fun deleteFavoriteTrack(trackId: Long)

    @Query(
        "SELECT t.* FROM tracks t " +
            "INNER JOIN favorite_tracks f ON t.trackId = f.trackId " +
            "ORDER BY f.addedAt DESC"
    )
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM favorite_tracks")
    fun getFavoriteTrackIds(): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE trackId = :trackId)")
    suspend fun isFavorite(trackId: Long): Boolean
}