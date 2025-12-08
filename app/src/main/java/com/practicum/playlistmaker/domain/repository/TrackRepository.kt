package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.models.Track

interface TrackRepository {
    fun search(query: String, callback: (Result<List<Track>>) -> Unit)
}