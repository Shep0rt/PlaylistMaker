package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.data.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.data.TrackRepositoryImpl
import com.practicum.playlistmaker.data.history.TrackHistorySharedPrefs
import com.practicum.playlistmaker.data.history.TracksHistoryDataSource
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.domain.repository.HistoryRepository
import com.practicum.playlistmaker.domain.repository.ThemeRepository
import com.practicum.playlistmaker.domain.repository.TrackRepository
import com.practicum.playlistmaker.util.Constants
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val TRACK_HISTORY_PREFS = named("history_search_track_prefs")
val THEME_PREFS = named("theme_prefs")

val dataModule = module {

    //История поиска треков
    single<TracksHistoryDataSource> {
        TrackHistorySharedPrefs(
            prefs = get(TRACK_HISTORY_PREFS),
            gson = get(),
            maxSize = Constants.HISTORY_MAX_SIZE
        )
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(
            historyDataSource = get()
        )
    }

    //Тема приложения
    single<ThemeRepository> {
        ThemeRepositoryImpl(
            prefs = get(THEME_PREFS)
        )
    }

    //Поиск треков
    single<TrackRepository> {
        TrackRepositoryImpl(
            api = get()
        )
    }

    //SharedPreferences
    single<SharedPreferences>(TRACK_HISTORY_PREFS) {
        get<Context>().getSharedPreferences(
            Constants.SEARCH_HISTORY_TRACK_PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    single<SharedPreferences>(THEME_PREFS) {
        get<Context>().getSharedPreferences(
            Constants.USER_SETTINGS_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    //GSON
    single { Gson() }

    //RETROFIT
    single {
        Retrofit.Builder()
            .baseUrl(Constants.ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ITunesApiService> {
        get<Retrofit>().create(ITunesApiService::class.java)
    }
}