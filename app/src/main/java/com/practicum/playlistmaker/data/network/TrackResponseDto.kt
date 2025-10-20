package com.practicum.playlistmaker.data.network

import com.google.gson.annotations.SerializedName

data class TrackResponseDto(@SerializedName ("trackName") val trackName:String?,
                            @SerializedName ("artistName") val artistName: String?,
                            @SerializedName ("artworkUrl100") val artworkUrl100: String?,
                            @SerializedName ("trackTimeMillis") val trackTimeMillis: Long?)