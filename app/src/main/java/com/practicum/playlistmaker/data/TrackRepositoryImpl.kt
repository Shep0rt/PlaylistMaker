package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.mappers.TrackMapper.toDomain
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.data.network.dto.TrackListResponseDto
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.TrackRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackRepositoryImpl(private val api: ITunesApiService) : TrackRepository {

    override fun search(query: String, callback: (Result<List<Track>>) -> Unit) {
        api.searchTracks(query).enqueue(object : Callback<TrackListResponseDto> {
            override fun onResponse(
                call: Call<TrackListResponseDto>,
                response: Response<TrackListResponseDto>
            ) {
                if (!response.isSuccessful) {
                    callback(Result.failure(Exception("Ошибка поиска треков: ${response.code()}")))
                    return
                }
                val body = response.body()
                val mapped = body?.results?.map { it.toDomain() }.orEmpty()
                callback(Result.success(mapped))
            }

            override fun onFailure(call: Call<TrackListResponseDto>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}