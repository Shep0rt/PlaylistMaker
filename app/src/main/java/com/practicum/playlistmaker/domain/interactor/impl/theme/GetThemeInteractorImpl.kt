package com.practicum.playlistmaker.domain.interactor.impl.theme

import com.practicum.playlistmaker.domain.interactor.theme.GetThemeInteractor
import com.practicum.playlistmaker.domain.repository.ThemeRepository

class GetThemeInteractorImpl(
    private val repo: ThemeRepository
) : GetThemeInteractor {
    override fun execute(): Boolean = repo.isDarkThemeEnabled()
}