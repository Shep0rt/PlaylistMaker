package com.practicum.playlistmaker.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.domain.interactor.history.ClearHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.history.GetHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.history.SaveToHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.search.SearchTracksInteractor

class SearchViewModelFactory(
    private val searchTracksInteractor: SearchTracksInteractor,
    private val getHistoryInteractor: GetHistoryInteractor,
    private val saveToHistoryInteractor: SaveToHistoryInteractor,
    private val clearHistoryInteractor: ClearHistoryInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(searchTracksInteractor,
                getHistoryInteractor,
                saveToHistoryInteractor,
                clearHistoryInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}