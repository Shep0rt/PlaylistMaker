package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.data.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.data.db.dao.TrackDao
import com.practicum.playlistmaker.data.db.mappers.PlaylistDbMapper
import com.practicum.playlistmaker.data.db.mappers.TrackDbMapper
import com.practicum.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.interactor.impl.playlist.AddToPlaylistResult
import com.practicum.playlistmaker.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val trackDao: TrackDao,
    private val playlistDbMapper: PlaylistDbMapper,
    private val trackDbMapper: TrackDbMapper
) : PlaylistRepository {

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverPath: String?
    ): Long {
        val entity = playlistDbMapper.mapToEntity(
            name = name,
            description = description,
            coverPath = coverPath,
            createdAt = System.currentTimeMillis()
        )
        return playlistDao.insertPlaylist(entity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().map { playlists ->
            playlists.map { playlistDbMapper.mapToDomain(it) }
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): AddToPlaylistResult {
        val isAlreadyInPlaylist = playlistTrackDao.isTrackInPlaylist(playlistId, track.id)
        if (isAlreadyInPlaylist) {
            return AddToPlaylistResult.AlreadyExists
        }

        val trackEntity = trackDbMapper.map(track)
        trackDao.insertTrackIfNotExists(trackEntity)

        playlistTrackDao.insertPlaylistTrack(
            PlaylistTrackEntity(
                playlistId = playlistId,
                trackId = track.id,
                addedAt = System.currentTimeMillis()
            )
        )

        return AddToPlaylistResult.Added
    }
}