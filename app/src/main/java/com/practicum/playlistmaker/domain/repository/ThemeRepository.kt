package com.practicum.playlistmaker.domain.repository

interface ThemeRepository {

    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}