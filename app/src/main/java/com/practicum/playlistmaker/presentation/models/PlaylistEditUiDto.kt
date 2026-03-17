package com.practicum.playlistmaker.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistEditUiDto(
    val id: Long,
    val name: String,
    val description: String?,
    val coverPath: String?
) : Parcelable
