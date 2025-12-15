package com.practicum.playlistmaker.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.interactor.theme.GetThemeInteractor
import com.practicum.playlistmaker.domain.interactor.theme.SetThemeInteractor

class SettingsViewModel(private val getThemeInteractor: GetThemeInteractor,
    private val setThemeInteractor: SetThemeInteractor
) : ViewModel() {

    private val darkMode = MutableLiveData<Boolean>()
    val darkModeEnabled: LiveData<Boolean> = darkMode

    init {
        darkMode.value = getThemeInteractor.execute()
    }

    fun setDarkMode(enabled: Boolean) {
        if (darkMode.value == enabled) return

        setThemeInteractor.execute(enabled)
        darkMode.value = enabled
    }
}