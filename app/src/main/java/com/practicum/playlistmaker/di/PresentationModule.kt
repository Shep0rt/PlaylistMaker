package com.practicum.playlistmaker.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.interactor.favorite.AddTrackToFavoritesInteractor
import com.practicum.playlistmaker.domain.interactor.favorite.GetFavoriteTracksInteractor
import com.practicum.playlistmaker.domain.interactor.favorite.IsFavoriteTrackInteractor
import com.practicum.playlistmaker.domain.interactor.favorite.RemoveTrackFromFavoritesInteractor
import com.practicum.playlistmaker.domain.interactor.history.ClearHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.history.GetHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.history.SaveToHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.search.SearchTracksInteractor
import com.practicum.playlistmaker.presentation.media.favorite.FavoriteTracksViewModel
import com.practicum.playlistmaker.presentation.media.playlist.PlaylistsViewModel
import com.practicum.playlistmaker.presentation.media.playlist.CreatePlaylistViewModel
import com.practicum.playlistmaker.presentation.media.playlist.PlaylistViewModel
import com.practicum.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.practicum.playlistmaker.domain.interactor.theme.GetThemeInteractor
import com.practicum.playlistmaker.domain.interactor.theme.SetThemeInteractor
import com.practicum.playlistmaker.presentation.player.PlayerViewModel
import com.practicum.playlistmaker.presentation.search.SearchViewModel
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import com.practicum.playlistmaker.domain.interactor.playlist.AddTrackToPlaylistInteractor
import com.practicum.playlistmaker.domain.interactor.playlist.CreatePlaylistInteractor
import com.practicum.playlistmaker.domain.interactor.playlist.GetPlaylistsInteractor
import com.practicum.playlistmaker.domain.interactor.playlist.GetPlaylistInteractor
import com.practicum.playlistmaker.domain.interactor.playlist.GetPlaylistTracksInteractor
import com.practicum.playlistmaker.domain.interactor.playlist.RemoveTrackFromPlaylistInteractor
import com.practicum.playlistmaker.domain.interactor.playlist.RemovePlaylistInteractor
import com.practicum.playlistmaker.domain.interactor.playlist.UpdatePlaylistInteractor
import com.practicum.playlistmaker.presentation.models.PlaylistEditUiDto

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
        PlayerViewModel(
            track = track,
            mediaPlayer = get(),
            addTrackToFavoritesInteractor = get<AddTrackToFavoritesInteractor>(),
            removeTrackFromFavoritesInteractor = get<RemoveTrackFromFavoritesInteractor>(),
            isFavoriteTrackInteractor = get<IsFavoriteTrackInteractor>(),
            getPlaylistsInteractor = get<GetPlaylistsInteractor>(),
            addTrackToPlaylistInteractor = get<AddTrackToPlaylistInteractor>()
        )
    }

    //FavoriteTracksViewModel
    viewModel {
        FavoriteTracksViewModel(
            getFavoriteTracksInteractor = get<GetFavoriteTracksInteractor>()
        )
    }

    //PlaylistsViewModel
    viewModel {
        PlaylistsViewModel(
            getPlaylistsInteractor = get<GetPlaylistsInteractor>()
        )
    }

    //CreatePlaylistViewModel
    viewModel { (playlist: PlaylistEditUiDto?) ->
        CreatePlaylistViewModel(
            playlist = playlist,
            createPlaylistInteractor = get<CreatePlaylistInteractor>(),
            updatePlaylistInteractor = get<UpdatePlaylistInteractor>()
        )
    }

    //PlaylistViewModel
    viewModel { (playlistId: Long) ->
        PlaylistViewModel(
            playlistId = playlistId,
            getPlaylistInteractor = get<GetPlaylistInteractor>(),
            getPlaylistTracksInteractor = get<GetPlaylistTracksInteractor>(),
            removeTrackFromPlaylistInteractor = get<RemoveTrackFromPlaylistInteractor>(),
            removePlaylistInteractor = get<RemovePlaylistInteractor>()
        )
    }

    factory { MediaPlayer() }
}
