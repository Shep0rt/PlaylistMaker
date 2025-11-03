package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.network.dto.TrackListResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {

    @GET("search")
    fun searchTracks(
        @Query("term") text: String,
        @Query("entity") entity: String = "song"
    ): Call<TrackListResponseDto>
}