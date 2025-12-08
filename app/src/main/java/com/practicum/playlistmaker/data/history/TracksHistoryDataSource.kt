package com.practicum.playlistmaker.data.history

import com.practicum.playlistmaker.domain.models.Track

interface TracksHistoryDataSource {
    fun get(): List<Track>
    fun add(track: Track)
    fun clear()
}