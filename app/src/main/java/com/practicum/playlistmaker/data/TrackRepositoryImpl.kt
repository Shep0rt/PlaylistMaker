package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.mappers.TrackMapper.toDomain
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val api: ITunesApiService) : TrackRepository {

    override fun search(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val response = api.searchTracks(query)
            val mapped = response.results?.map { it.toDomain() }.orEmpty()
            emit(Result.success(mapped))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
