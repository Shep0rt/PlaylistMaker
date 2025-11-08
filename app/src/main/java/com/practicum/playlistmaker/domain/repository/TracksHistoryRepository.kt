package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.models.Track

interface TracksHistoryRepository {
    fun get(): List<Track>
    fun add(track: Track)
    fun clear()
}