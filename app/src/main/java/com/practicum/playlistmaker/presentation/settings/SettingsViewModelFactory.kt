package com.practicum.playlistmaker.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.domain.interactor.theme.GetThemeInteractor
import com.practicum.playlistmaker.domain.interactor.theme.SetThemeInteractor

class SettingsViewModelFactory(
    private val getThemeInteractor: GetThemeInteractor,
    private val setThemeInteractor: SetThemeInteractor
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(getThemeInteractor, setThemeInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}