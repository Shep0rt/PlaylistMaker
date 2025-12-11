package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.interactor.history.ClearHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.history.GetHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.history.SaveToHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.impl.history.ClearHistoryInteractorImpl
import com.practicum.playlistmaker.domain.interactor.impl.history.GetHistoryInteractorImpl
import com.practicum.playlistmaker.domain.interactor.impl.history.SaveToHistoryInteractorImpl
import com.practicum.playlistmaker.domain.interactor.impl.search.SearchTracksInteractorImpl
import com.practicum.playlistmaker.domain.interactor.impl.theme.GetThemeInteractorImpl
import com.practicum.playlistmaker.domain.interactor.impl.theme.SetThemeInteractorImpl
import com.practicum.playlistmaker.domain.interactor.search.SearchTracksInteractor
import com.practicum.playlistmaker.domain.interactor.theme.GetThemeInteractor
import com.practicum.playlistmaker.domain.interactor.theme.SetThemeInteractor
import com.practicum.playlistmaker.domain.repository.HistoryRepository
import com.practicum.playlistmaker.domain.repository.ThemeRepository
import com.practicum.playlistmaker.domain.repository.TrackRepository
import org.koin.dsl.module

val domainModule = module {

    //История поиска треков
    factory<ClearHistoryInteractor> {
        ClearHistoryInteractorImpl(
            repo = get<HistoryRepository>()
        )
    }

    factory<GetHistoryInteractor> {
        GetHistoryInteractorImpl(
            repo = get<HistoryRepository>()
        )
    }

    factory<SaveToHistoryInteractor> {
        SaveToHistoryInteractorImpl(
            repo = get<HistoryRepository>()
        )
    }

    //Поиск треков
    factory<SearchTracksInteractor> {
        SearchTracksInteractorImpl(
            repo = get<TrackRepository>()
        )
    }

    //Тема приложения
    factory<GetThemeInteractor> {
        GetThemeInteractorImpl(
            repo = get<ThemeRepository>()
        )
    }

    factory<SetThemeInteractor> {
        SetThemeInteractorImpl(
            repo = get<ThemeRepository>()
        )
    }
}