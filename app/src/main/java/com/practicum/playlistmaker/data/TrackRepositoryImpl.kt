package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.mappers.TrackMapper.toDomain
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.data.network.dto.TrackListResponseDto
import com.practicum.playlistmaker.domain.common.DataResult
import com.practicum.playlistmaker.domain.common.ErrorType
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.TrackRepository
import com.practicum.playlistmaker.domain.repository.TracksHistoryRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackRepositoryImpl(private val api: ITunesApiService,
                          private val historyDataSource: TracksHistoryRepository
) : TrackRepository {

    override fun search(query: String, callback: (DataResult<List<Track>>) -> Unit) {
        api.searchTracks(query).enqueue(object : Callback<TrackListResponseDto> {
            override fun onResponse(
                call: Call<TrackListResponseDto>,
                response: Response<TrackListResponseDto>
            ) {
                if (!response.isSuccessful) {
                    callback(DataResult(isSuccess = false, error = ErrorType.HTTP, httpCode = response.code()))
                    return
                }
                val body = response.body()
                val mapped = body?.results?.map { it.toDomain() }.orEmpty()
                callback(DataResult(isSuccess = true, data = mapped))
            }

            override fun onFailure(call: Call<TrackListResponseDto>, t: Throwable) {
                callback(DataResult(isSuccess = false, error = ErrorType.NETWORK))
            }
        })
    }

    override fun history(): List<Track> = historyDataSource.get()

    override fun addToHistory(track: Track) = historyDataSource.add(track)

    override fun clearHistory() = historyDataSource.clear()
}