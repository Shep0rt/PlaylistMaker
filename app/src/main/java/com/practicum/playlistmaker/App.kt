package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.SharedPrefsThemeRepository
import com.practicum.playlistmaker.settings.ThemeRepository


class App : Application() {

    lateinit var themeRepository: ThemeRepository
        private set

    override fun onCreate() {
        super.onCreate()

        themeRepository = SharedPrefsThemeRepository(this)
        val isDarkThemeEnabled = themeRepository.isDarkThemeEnabled()
        applyTheme(isDarkThemeEnabled)
    }

    //Изменение тем
    fun switchTheme(darkThemeEnabled: Boolean) {
        themeRepository.setDarkThemeEnabled(darkThemeEnabled)
        applyTheme(darkThemeEnabled)
    }

    private fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}