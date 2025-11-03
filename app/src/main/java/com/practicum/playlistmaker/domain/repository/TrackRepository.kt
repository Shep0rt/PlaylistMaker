package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.common.DataResult
import com.practicum.playlistmaker.domain.models.Track

interface TrackRepository {
    fun search(query: String, callback: (DataResult<List<Track>>) -> Unit)
    fun history(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
}