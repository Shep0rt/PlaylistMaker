package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.data.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.data.db.dao.TrackDao
import com.practicum.playlistmaker.data.db.dao.FavoriteTrackDao
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
    private val favoriteTrackDao: FavoriteTrackDao,
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

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylistById(playlistId).map { playlist ->
            playlist?.let { playlistDbMapper.mapToDomain(it) }
        }
    }

    override fun getPlaylistTracks(playlistId: Long): Flow<List<Track>> {
        return playlistTrackDao.getTracksForPlaylist(playlistId).map { tracks ->
            tracks.map { entity -> trackDbMapper.map(entity, false) }
        }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistTrackDao.deletePlaylistTrack(playlistId, trackId)
        val isTrackInAnyPlaylist = playlistTrackDao.isTrackInAnyPlaylist(trackId)
        val isFavorite = favoriteTrackDao.isFavorite(trackId)
        if (!isTrackInAnyPlaylist && !isFavorite) {
            trackDao.deleteTrack(trackId)
        }
    }

    override suspend fun removePlaylist(playlistId: Long) {
        val trackIds = playlistTrackDao.getTrackIds(playlistId).distinct()
        playlistTrackDao.deletePlaylistTracks(playlistId)
        playlistDao.deletePlaylist(playlistId)
        trackIds.forEach { trackId ->
            val isTrackInAnyPlaylist = playlistTrackDao.isTrackInAnyPlaylist(trackId)
            val isFavorite = favoriteTrackDao.isFavorite(trackId)
            if (!isTrackInAnyPlaylist && !isFavorite) {
                trackDao.deleteTrack(trackId)
            }
        }
    }

    override suspend fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String?,
        coverPath: String?
    ) {
        playlistDao.updatePlaylistFields(
            playlistId = playlistId,
            name = name,
            description = description,
            coverPath = coverPath
        )
    }
}
