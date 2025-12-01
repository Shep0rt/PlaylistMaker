package com.practicum.playlistmaker.presentation.search

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.interactor.history.ClearHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.history.GetHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.history.SaveToHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.search.SearchTracksInteractor
import java.util.concurrent.Executors
import com.practicum.playlistmaker.presentation.mappers.DomainToUiMapper
import com.practicum.playlistmaker.presentation.mappers.UiToDomainMapper
import com.practicum.playlistmaker.presentation.models.TrackUiDto

class SearchViewModel(
    private val searchTracksInteractor: SearchTracksInteractor,
    private val getHistoryInteractor: GetHistoryInteractor,
    private val saveToHistoryInteractor: SaveToHistoryInteractor,
    private val clearHistoryInteractor: ClearHistoryInteractor
) : ViewModel() {

    private val state = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = state

    private val bg = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private var clickAllowed = true
    private val clickHandler = Handler(Looper.getMainLooper())

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    fun onQueryChanged(text: String) {
        if (text.isBlank()) {
            cancelDebounce()
            loadHistory()
            return
        }

        state.value = SearchState.Idle
        debounceSearch(text)
    }

    fun loadHistory() {
        bg.execute {
            val domain = getHistoryInteractor.execute()
            val ui = DomainToUiMapper.listToUi(domain)
            main.post {
                if (ui.isEmpty()) {
                    state.value = SearchState.Idle
                } else {
                    state.value = SearchState.History(ui)
                }
            }
        }
    }

    fun onTrackClicked(track: TrackUiDto) {
        if (!clickAllowed) return

        clickAllowed = false
        clickHandler.postDelayed({ clickAllowed = true }, CLICK_DEBOUNCE_DELAY)

        bg.execute {
            val domain = UiToDomainMapper.trackToDomain(track)
            saveToHistoryInteractor.execute(domain)
        }
    }

    fun clearHistory() {
        bg.execute {
            clearHistoryInteractor.execute()
            main.post { state.value = SearchState.History(emptyList()) }
        }
    }

    fun retry(query: String) {
        if (query.isNotBlank()) performSearch(query)
    }

    private fun debounceSearch(query: String) {
        cancelDebounce()

        val runnable = Runnable {
            performSearch(query)
        }
        searchRunnable = runnable
        searchHandler.postDelayed(runnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun cancelDebounce() {
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
    }

    private fun performSearch(query: String) {
        state.postValue(SearchState.Loading)

        searchTracksInteractor.execute(query) { result ->
            main.post {
                result.onSuccess { domain ->
                    val list = domain.map { DomainToUiMapper.trackToUi(it) }
                    if (list.isEmpty()) {
                        state.value = SearchState.Empty
                    } else {
                        state.value = SearchState.Content(list)
                    }
                }.onFailure {
                    state.value = SearchState.Error
                }
            }
        }
    }
}