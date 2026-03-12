package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistTrack(playlistTrack: PlaylistTrackEntity)

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getTrackCount(playlistId: Long): Int

    @Query(
        "SELECT EXISTS(" +
            "SELECT 1 FROM playlist_tracks " +
            "WHERE playlistId = :playlistId AND trackId = :trackId" +
            ")"
    )
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean

    @Query("SELECT trackId FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getTrackIds(playlistId: Long): List<Long>
}