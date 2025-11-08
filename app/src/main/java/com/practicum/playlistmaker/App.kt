package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)

        val dark = Creator.getThemeInteractor.execute()
        AppCompatDelegate.setDefaultNightMode(
            if (dark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}