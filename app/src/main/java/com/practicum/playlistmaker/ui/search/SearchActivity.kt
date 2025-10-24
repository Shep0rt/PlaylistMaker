package com.practicum.playlistmaker.ui.search

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.history.TracksHistoryStorage
import com.practicum.playlistmaker.data.mappers.TrackMapper
import com.practicum.playlistmaker.data.network.ITunesApi
import com.practicum.playlistmaker.data.network.TrackListResponseDto
import com.practicum.playlistmaker.model.Track
import com.practicum.playlistmaker.util.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var searchText: String = SEARCH_TEXT_DEF
    private var lastFailedRequest: String? = null
    private var currentRequest: String? = null
    private lateinit var toolbar: MaterialToolbar
    private lateinit var searchEditText: EditText
    private lateinit var clearButton:  ImageView
    private lateinit var searchRecycler: RecyclerView
    private lateinit var searchAdapter: TrackAdapter

    private lateinit var searchNotResultPlaceholder: View
    private lateinit var networkErrorPlaceholder: View
    private lateinit var retrySearchButton: View
    private lateinit var historyStorage: TracksHistoryStorage
    private lateinit var prefsHistoryListener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var searchHistory: View
    private lateinit var historyRecycler: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var clearSearchHistoryButton: View


    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesApi = retrofit.create(ITunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val rootView = findViewById<View>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(
                top = statusBarInsets.top,
                bottom = navBarInsets.bottom
            )
            insets
        }

        toolbar = findViewById(R.id.search_toolbar)
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        searchRecycler = findViewById(R.id.searchRecyclerView)
        searchNotResultPlaceholder = findViewById(R.id.not_result_placeholder)
        networkErrorPlaceholder = findViewById(R.id.network_error_placeholder)
        retrySearchButton = networkErrorPlaceholder.findViewById(R.id.retrySearchButton)
        searchHistory = findViewById(R.id.search_history)
        historyRecycler = searchHistory.findViewById(R.id.historyRecycler)
        clearSearchHistoryButton = searchHistory.findViewById(R.id.clearHistoryButton)
        historyStorage = TracksHistoryStorage(this)

        searchRecycler.visibility = View.GONE
        searchNotResultPlaceholder.visibility = View.GONE
        networkErrorPlaceholder.visibility = View.GONE
        searchHistory.visibility = View.GONE

        searchAdapter = TrackAdapter(emptyList()) { clickedTrack ->
            historyStorage.add(clickedTrack)
        }
        searchRecycler.adapter = searchAdapter

        historyAdapter = TrackAdapter(emptyList(), null)
        historyRecycler.adapter = historyAdapter

        //Действия при клике на кнопку "Назад" внутри раздела "Поиск"
        toolbar.setNavigationOnClickListener {
            finish()
        }

        //Работа поля поиска
        //Дейтсвие при клике на кнопку "Х" внутри поля поиска
        clearButton.setOnClickListener {
            searchEditText.text.clear()

            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)

            searchEditText.clearFocus()

            searchRecycler.visibility = View.GONE
            networkErrorPlaceholder.visibility = View.GONE
            searchNotResultPlaceholder.visibility = View.GONE
            tryShowHistory()
        }

        //Действия с текстом внутри поля поиска
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()

                if (s.isNullOrBlank()) {
                    tryShowHistory()
                } else {
                    showHistory(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = s?.toString() ?: SEARCH_TEXT_DEF
            }
        }
        searchEditText.addTextChangedListener(searchTextWatcher)

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isNullOrBlank()) {
                tryShowHistory()
            } else if (!hasFocus) {
                showHistory(false)
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) searchTracks(query)
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                searchEditText.clearFocus()
                true
            } else false
        }

        //Действие при клике на кнопку "Обновить" на networkErrorPlaceholder
        retrySearchButton.setOnClickListener {
            lastFailedRequest?.let { query ->
                networkErrorPlaceholder.visibility = View.GONE
                searchTracks(query)
            }
        }

        //Подписка на изменения истории в SharedPreferences
        prefsHistoryListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == TracksHistoryStorage.KEY_HISTORY_TRACKS) {
                tryShowHistory()
            }
        }
        historyStorage.getPrefs()
            .registerOnSharedPreferenceChangeListener(prefsHistoryListener)

        //Действие при клике на кнопку "Очистить историю"
        clearSearchHistoryButton.setOnClickListener {
            historyStorage.clear()
        }
    }

    //Поиск треков через iTunes API
    private fun searchTracks(text: String) {
        currentRequest = text
        lastFailedRequest = null

        iTunesApi.searchTracks(text = text)
            .enqueue(object : Callback<TrackListResponseDto> {
                override fun onResponse(
                    call: Call<TrackListResponseDto>,
                    response: retrofit2.Response<TrackListResponseDto>
                ) {
                    handleResponse(response)
                }

                override fun onFailure(call: Call<TrackListResponseDto>, t: Throwable) {
                    handleFailure(t)
                }
            })
    }
    
    //Разбираем ответ от iTunes API
    private fun handleResponse(response: retrofit2.Response<TrackListResponseDto>) {
        if (response.isSuccessful) {
            val tracksResponse = response.body()?.results.orEmpty()
            val tracks = TrackMapper.mapList(tracksResponse)
            if (tracks.isNotEmpty()) {
                showTrackList(tracks)
            } else {
                showEmptyResult()
            }
            lastFailedRequest = null
        } else {
            lastFailedRequest = currentRequest
            showNetworkError()
        }
    }

    //Записываем ошибку в лог если произошла сетевая ошибка при обращении в iTunes API
    private fun handleFailure(t: Throwable) {
        showNetworkError()
        lastFailedRequest = currentRequest
    }

    //Обновляем адаптер списком треков
    private fun showTrackList(tracks: List<Track>) {
        showHistory(false)
        searchNotResultPlaceholder.visibility = View.GONE
        networkErrorPlaceholder.visibility= View.GONE
        searchRecycler.visibility = View.VISIBLE

        searchAdapter.submitList(tracks)
    }

    //Обновляем адаптер пустым списком если результаты поиска отсутствуют
    private fun showEmptyResult() {
        showHistory(false)
        searchRecycler.visibility = View.GONE
        networkErrorPlaceholder.visibility = View.GONE
        searchNotResultPlaceholder.visibility = View.VISIBLE

        searchAdapter.submitList(emptyList())
    }

    private fun showNetworkError() {
        showHistory(false)
        searchRecycler.visibility = View.GONE
        searchNotResultPlaceholder.visibility = View.GONE
        networkErrorPlaceholder.visibility = View.VISIBLE
    }

    private fun tryShowHistory() {
        val items = historyStorage.get()
        if (searchEditText.text.isNullOrBlank() && items.isNotEmpty()) {
            historyAdapter.submitList(items)
            showHistory(true)
            searchRecycler.visibility = View.GONE
            searchNotResultPlaceholder.visibility = View.GONE
            networkErrorPlaceholder.visibility = View.GONE
        } else {
            showHistory(false)
        }
    }

    private fun showHistory(show: Boolean) {
        searchHistory.visibility = if (show) {
            View.VISIBLE
        } else View.GONE
    }

    //Сохраняем внесенные изменения пользователем, на данной activity
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    //Восстанавливаем внесенные изменения пользователем, на данной activity
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT, SEARCH_TEXT_DEF)
    }

    override fun onDestroy() {
        super.onDestroy()
        historyStorage.getPrefs()
            .unregisterOnSharedPreferenceChangeListener(prefsHistoryListener)
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_TEXT_DEF = ""
    }
}