package com.practicum.playlistmaker.domain.interactor.impl.theme

import com.practicum.playlistmaker.domain.interactor.theme.SetThemeInteractor
import com.practicum.playlistmaker.domain.repository.ThemeRepository

class SetThemeInteractorImpl(
    private val repo: ThemeRepository
) : SetThemeInteractor {
    override fun execute(enabled: Boolean) = repo.setDarkThemeEnabled(enabled)
}