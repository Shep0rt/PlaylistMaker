package com.practicum.playlistmaker.presentation.settings

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.interactor.theme.GetThemeInteractor
import com.practicum.playlistmaker.domain.interactor.theme.SetThemeInteractor

class SettingsViewModel(private val getThemeInteractor: GetThemeInteractor,
    private val setThemeInteractor: SetThemeInteractor
) : ViewModel() {

    fun isDarkMode(): Boolean {
        return getThemeInteractor.execute()
    }

    fun setDarkMode(enabled: Boolean) {
        setThemeInteractor.execute(enabled)
    }
}