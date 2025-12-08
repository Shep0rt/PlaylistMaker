package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.models.Track

interface HistoryRepository {
    fun history(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
}