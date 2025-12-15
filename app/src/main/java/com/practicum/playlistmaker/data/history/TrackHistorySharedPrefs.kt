package com.practicum.playlistmaker.data.history

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.util.Constants

class TrackHistorySharedPrefs(
    private val prefs: SharedPreferences,
    private val gson: Gson = Gson(),
    private val maxSize: Int = Constants.HISTORY_MAX_SIZE
) : TracksHistoryDataSource {

    override fun get(): List<Track> {
        val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson<List<Track>>(json, type) ?: emptyList()
    }

    override fun add(track: Track) {
        val list = get().toMutableList()
        list.removeAll { it.id == track.id }
        list.add(0, track)
        if (list.size > maxSize) list.subList(maxSize, list.size).clear()
        save(list)
    }

    override fun clear() {
        prefs.edit { remove(KEY_HISTORY) }
    }

    private fun save(list: List<Track>) {
        val json = gson.toJson(list)
        prefs.edit { putString(KEY_HISTORY, json) }
    }

    private companion object {
        const val KEY_HISTORY = "search_history"
    }
}