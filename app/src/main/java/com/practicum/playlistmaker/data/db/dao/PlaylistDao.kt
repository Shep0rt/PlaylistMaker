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

    @Query(
        "SELECT p.*, COUNT(pt.trackId) AS trackCount " +
            "FROM playlists p " +
            "LEFT JOIN playlist_tracks pt ON p.id = pt.playlistId " +
            "WHERE p.id = :playlistId " +
            "GROUP BY p.id"
    )
    fun getPlaylistById(playlistId: Long): Flow<PlaylistWithTrackCount?>

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query(
        "UPDATE playlists " +
            "SET name = :name, description = :description, coverPath = :coverPath " +
            "WHERE id = :playlistId"
    )
    suspend fun updatePlaylistFields(
        playlistId: Long,
        name: String,
        description: String?,
        coverPath: String?
    )
}
