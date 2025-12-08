package com.practicum.playlistmaker.creator

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.data.HistoryRepositoryImpl
import com.practicum.playlistmaker.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.practicum.playlistmaker.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.data.history.TracksHistoryDataSource
import com.practicum.playlistmaker.data.history.TrackHistorySharedPrefs
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
import com.practicum.playlistmaker.domain.repository.ThemeRepository
import com.practicum.playlistmaker.domain.interactor.theme.GetThemeInteractor
import com.practicum.playlistmaker.domain.interactor.theme.SetThemeInteractor
import com.practicum.playlistmaker.domain.interactor.impl.theme.GetThemeInteractorImpl
import com.practicum.playlistmaker.domain.interactor.impl.theme.SetThemeInteractorImpl
import com.practicum.playlistmaker.domain.repository.HistoryRepository

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
    private val historyDataSource: TracksHistoryDataSource by lazy {
        TrackHistorySharedPrefs(
            context = appContext,
            gson = Gson(),
            maxSize = Constants.HISTORY_MAX_SIZE
        )
    }

    //Репозитории
    private val trackRepository: TrackRepository by lazy {
        TrackRepositoryImpl(
            api = iTunesApi
        )
    }

    private val themeRepository: ThemeRepository by lazy {
        ThemeRepositoryImpl(appContext)
    }

    private val historyRepository: HistoryRepository by lazy {
        HistoryRepositoryImpl(historyDataSource)
    }
    //Интеракторы
    val searchTracksInteractor: SearchTracksInteractor by lazy {
        SearchTracksInteractorImpl(trackRepository)
    }

    val getHistoryInteractor: GetHistoryInteractor by lazy {
        GetHistoryInteractorImpl(historyRepository)
    }

    val addToHistoryInteractor: SaveToHistoryInteractor by lazy {
        SaveToHistoryInteractorImpl(historyRepository)
    }

    val clearHistoryInteractor: ClearHistoryInteractor by lazy {
        ClearHistoryInteractorImpl(historyRepository)
    }

    val getThemeInteractor: GetThemeInteractor by lazy {
        GetThemeInteractorImpl(themeRepository)
    }

    val setThemeInteractor: SetThemeInteractor by lazy {
        SetThemeInteractorImpl(themeRepository)
    }
}