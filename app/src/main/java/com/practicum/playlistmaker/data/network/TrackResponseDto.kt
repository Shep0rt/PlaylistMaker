package com.practicum.playlistmaker.data.network

import com.google.gson.annotations.SerializedName

data class TrackResponseDto(@SerializedName ("trackName") val trackName:String?,
                            @SerializedName ("artistName") val artistName: String?,
                            @SerializedName ("artworkUrl100") val artworkUrl100: String?,
                            @SerializedName ("trackTimeMillis") val trackTimeMillis: Long?,
                            @SerializedName ("trackId") val id: Long?,
                            @SerializedName ("collectionName") val collectionName: String?,
                            @SerializedName ("releaseDate") val releaseDate: String?,
                            @SerializedName ("primaryGenreName") val primaryGenreName: String?,
                            @SerializedName ("country") val country: String?
)