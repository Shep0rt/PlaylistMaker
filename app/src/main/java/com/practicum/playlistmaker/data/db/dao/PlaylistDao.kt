package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistWithTrackCount
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query(
        "SELECT p.*, COUNT(pt.trackId) AS trackCount " +
            "FROM playlists p " +
            "LEFT JOIN playlist_tracks pt ON p.id = pt.playlistId " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt DESC"
    )
    fun getPlaylists(): Flow<List<PlaylistWithTrackCount>>
}