package com.practicum.playlistmaker.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.practicum.playlistmaker.domain.repository.ThemeRepository

class ThemeRepositoryImpl(
    private val prefs: SharedPreferences
) : ThemeRepository {

    //Получить текущее значение настройки, если пусто то записать false и вернуть
    override fun isDarkThemeEnabled(): Boolean {
        return prefs.getBoolean(EDIT_THEME_KEY, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(EDIT_THEME_KEY, enabled) }
    }

    companion object {
        const val EDIT_THEME_KEY = "key_for_theme"
    }
}