package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun search(query: String): Flow<Result<List<Track>>>
}