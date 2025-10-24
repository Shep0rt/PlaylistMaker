package com.practicum.playlistmaker.settings

interface ThemeRepository {

    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}