package com.practicum.playlistmaker.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.interactor.history.ClearHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.history.GetHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.history.SaveToHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.search.SearchTracksInteractor
import com.practicum.playlistmaker.presentation.mappers.DomainToUiMapper
import com.practicum.playlistmaker.presentation.mappers.UiToDomainMapper
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import com.practicum.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksInteractor: SearchTracksInteractor,
    private val getHistoryInteractor: GetHistoryInteractor,
    private val saveToHistoryInteractor: SaveToHistoryInteractor,
    private val clearHistoryInteractor: ClearHistoryInteractor
) : ViewModel() {

    private val state = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = state

    private var lastSearchText: String? = null

    private val trackSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { text ->
        performSearch(text)
    }

    private val onTrackClickDebounce = debounce<TrackUiDto>(CLICK_DEBOUNCE_DELAY, viewModelScope, false) { track ->
        saveTrackToHistory(track)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    fun onQueryChanged(text: String) {
        if (lastSearchText == text) return
        lastSearchText = text

        if (text.isBlank()) {
            loadHistory()
        } else {
            state.value = SearchState.Idle
            trackSearchDebounce(text)
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            val domain = getHistoryInteractor.execute()
            val ui = DomainToUiMapper.listToUi(domain)
            state.value = if (ui.isEmpty()) SearchState.Idle else SearchState.History(ui)
        }
    }

    fun onTrackClicked(track: TrackUiDto) {
        onTrackClickDebounce(track)
    }

    private fun saveTrackToHistory(track: TrackUiDto) {
        viewModelScope.launch {
            val domain = UiToDomainMapper.trackToDomain(track)
            saveToHistoryInteractor.execute(domain)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            clearHistoryInteractor.execute()
            state.value = SearchState.History(emptyList())
        }
    }

    fun retry(query: String) {
        if (query.isNotBlank()) performSearch(query)
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) return

        state.value = SearchState.Loading

        viewModelScope.launch {
            searchTracksInteractor
                .execute(query)
                .collect { result ->
                    result.onSuccess { domain ->
                        val list = domain.map { DomainToUiMapper.trackToUi(it) }
                        state.value = if (list.isEmpty()) SearchState.Empty else SearchState.Content(list)
                    }.onFailure {
                        state.value = SearchState.Error
                    }
                }
        }
    }
}