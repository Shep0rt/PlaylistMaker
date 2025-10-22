package com.practicum.playlistmaker.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

const val USER_SETTINGS_PREFERENCES = "user_settings_preferences"
const val EDIT_THEME_KEY = "key_for_theme"

class SharedPrefsThemeRepository(
    context: Context
) : ThemeRepository {

    private val prefs = context.getSharedPreferences(USER_SETTINGS_PREFERENCES, MODE_PRIVATE)

    //Получить текущее значение настройки, если пусто то записать false и вернуть
    override fun isDarkThemeEnabled(): Boolean {
        return prefs.getBoolean(EDIT_THEME_KEY, false)
    }


    override fun setDarkThemeEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(EDIT_THEME_KEY, enabled) }
    }

}