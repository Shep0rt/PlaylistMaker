package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.interactor.history.ClearHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.history.GetHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.history.SaveToHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.search.SearchTracksInteractor
import com.practicum.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.practicum.playlistmaker.domain.interactor.theme.GetThemeInteractor
import com.practicum.playlistmaker.domain.interactor.theme.SetThemeInteractor
import com.practicum.playlistmaker.presentation.player.PlayerViewModel
import com.practicum.playlistmaker.presentation.search.SearchViewModel
import com.practicum.playlistmaker.presentation.models.TrackUiDto

val presentationModule = module {

    //SettingsViewModel
    viewModel {
        SettingsViewModel(
            getThemeInteractor = get<GetThemeInteractor>(),
            setThemeInteractor = get<SetThemeInteractor>()
        )
    }

    //SearchViewModel
    viewModel {
        SearchViewModel(
            searchTracksInteractor = get<SearchTracksInteractor>(),
            getHistoryInteractor = get<GetHistoryInteractor>(),
            saveToHistoryInteractor = get<SaveToHistoryInteractor>(),
            clearHistoryInteractor = get<ClearHistoryInteractor>()
        )
    }

    //PlayerViewModel
    viewModel { (track: TrackUiDto) ->
        PlayerViewModel(track = track)
    }
}