package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.history.TracksHistoryDataSource
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.HistoryRepository

class HistoryRepositoryImpl(private val historyDataSource: TracksHistoryDataSource): HistoryRepository {

    override fun history(): List<Track> = historyDataSource.get()

    override fun addToHistory(track: Track) = historyDataSource.add(track)

    override fun clearHistory() = historyDataSource.clear()
}