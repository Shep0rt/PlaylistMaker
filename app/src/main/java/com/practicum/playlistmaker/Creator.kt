package com.practicum.playlistmaker

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.practicum.playlistmaker.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.domain.repository.TracksHistoryRepository
import com.practicum.playlistmaker.data.TrackHistoryRepositoryImpl
import com.practicum.playlistmaker.domain.repository.TrackRepository
import com.practicum.playlistmaker.data.TrackRepositoryImpl
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.domain.interactor.search.SearchTracksInteractor
import com.practicum.playlistmaker.domain.interactor.impl.search.SearchTracksInteractorImpl
import com.practicum.playlistmaker.domain.interactor.history.GetHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.impl.history.GetHistoryInteractorImpl
import com.practicum.playlistmaker.domain.interactor.history.ClearHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.impl.history.ClearHistoryInteractorImpl
import com.practicum.playlistmaker.domain.interactor.history.SaveToHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.impl.history.SaveToHistoryInteractorImpl
import com.practicum.playlistmaker.domain.interactor.impl.player.GetPlayerStateInteractorImpl
import com.practicum.playlistmaker.domain.interactor.impl.player.GetPositionInteractorImpl
import com.practicum.playlistmaker.domain.interactor.impl.player.PauseTrackInteractorImpl
import com.practicum.playlistmaker.domain.interactor.impl.player.PlayTrackInteractorImpl
import com.practicum.playlistmaker.domain.interactor.impl.player.PrepareTrackInteractorImpl
import com.practicum.playlistmaker.domain.interactor.impl.player.SetPlayerEventsInteractorImpl
import com.practicum.playlistmaker.domain.interactor.player.GetPlayerStateInteractor
import com.practicum.playlistmaker.domain.interactor.player.GetPositionInteractor
import com.practicum.playlistmaker.domain.interactor.player.PauseTrackInteractor
import com.practicum.playlistmaker.domain.interactor.player.PlayTrackInteractor
import com.practicum.playlistmaker.domain.interactor.player.PrepareTrackInteractor
import com.practicum.playlistmaker.domain.repository.PlayerRepository
import com.practicum.playlistmaker.domain.interactor.player.SetPlayerEventsInteractor
import com.practicum.playlistmaker.domain.repository.ThemeRepository
import com.practicum.playlistmaker.domain.interactor.theme.GetThemeInteractor
import com.practicum.playlistmaker.domain.interactor.theme.SetThemeInteractor
import com.practicum.playlistmaker.domain.interactor.impl.theme.GetThemeInteractorImpl
import com.practicum.playlistmaker.domain.interactor.impl.theme.SetThemeInteractorImpl

object Creator {

    //Инициализация из Application
    private lateinit var appContext: Context
    fun init(context: Context) { appContext = context.applicationContext }

    //Network
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val iTunesApi: ITunesApiService by lazy {
        retrofit.create(ITunesApiService::class.java)
    }

    //Локальное хранилище
    private val historyDataSource: TracksHistoryRepository by lazy {
        TrackHistoryRepositoryImpl(
            context = appContext,
            gson = Gson(),
            maxSize = Constants.HISTORY_MAX_SIZE
        )
    }

    //Репозитории
    private val trackRepository: TrackRepository by lazy {
        TrackRepositoryImpl(
            api = iTunesApi,
            historyDataSource = historyDataSource
        )
    }

    private val themeRepository: ThemeRepository by lazy {
        ThemeRepositoryImpl(appContext)
    }

    private val playerRepository: PlayerRepository by lazy {
        PlayerRepositoryImpl(
            mediaPlayer = MediaPlayer()
        )
    }
    //Интеракторы
    val searchTracksInteractor: SearchTracksInteractor by lazy {
        SearchTracksInteractorImpl(trackRepository)
    }

    val getHistoryInteractor: GetHistoryInteractor by lazy {
        GetHistoryInteractorImpl(trackRepository)
    }

    val addToHistoryInteractor: SaveToHistoryInteractor by lazy {
        SaveToHistoryInteractorImpl(trackRepository)
    }

    val clearHistoryInteractor: ClearHistoryInteractor by lazy {
        ClearHistoryInteractorImpl(trackRepository)
    }

    val getPlayerStateInteractor: GetPlayerStateInteractor by lazy {
        GetPlayerStateInteractorImpl(playerRepository)
    }

    val getPositionInteractor: GetPositionInteractor by lazy {
        GetPositionInteractorImpl(playerRepository)
    }

    val pauseTrackInteractor: PauseTrackInteractor by lazy {
        PauseTrackInteractorImpl(playerRepository)
    }

    val playTrackInteractor: PlayTrackInteractor by lazy {
        PlayTrackInteractorImpl(playerRepository)
    }

    val prepareTrackInteractor: PrepareTrackInteractor by lazy {
        PrepareTrackInteractorImpl(playerRepository)
    }

    val setPlayerEventsInteractor: SetPlayerEventsInteractor by lazy {
        SetPlayerEventsInteractorImpl(playerRepository)
    }

    val getThemeInteractor: GetThemeInteractor by lazy {
        GetThemeInteractorImpl(themeRepository)
    }

    val setThemeInteractor: SetThemeInteractor by lazy {
        SetThemeInteractorImpl(themeRepository)
    }
}