package com.practicum.playlistmaker.data.history

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.model.Track
import androidx.core.content.edit

class TracksHistoryStorage(
    context: Context,
    private val gson: Gson = Gson(),
    private val maxSize: Int = 10
) {
    private val prefs = context.getSharedPreferences(PREFS_SEARCH_TRACKS_HISTORY_NAME, Context.MODE_PRIVATE)

    fun get(): List<Track> {
        val json = prefs.getString(KEY_HISTORY_TRACKS, null) ?: return emptyList()
        val objectType = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, objectType) ?: emptyList()
    }

    fun add(track: Track) {
        val historyList = get().toMutableList()
        historyList.removeAll { it.id == track.id }
        historyList.add(0, track)
        if (historyList.size > maxSize){
            historyList.subList(maxSize, historyList.size).clear()
        }
        save(historyList)
    }

    fun clear() {
        prefs.edit { remove(KEY_HISTORY_TRACKS) }
    }

    fun getPrefs(): SharedPreferences{
        return prefs
    }

    private fun save(listTracks: List<Track>) {
        val json = gson.toJson(listTracks)
        prefs.edit { putString(KEY_HISTORY_TRACKS, json) }
    }

    companion object {
        private const val PREFS_SEARCH_TRACKS_HISTORY_NAME = "search_tracks_history"
        const val KEY_HISTORY_TRACKS = "search_history"
    }
}