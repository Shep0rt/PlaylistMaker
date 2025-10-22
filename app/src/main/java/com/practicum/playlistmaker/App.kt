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

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    //Изменение тем
    fun switchTheme(darkThemeEnabled: Boolean) {
        themeRepository.setDarkThemeEnabled(darkThemeEnabled)
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }


}