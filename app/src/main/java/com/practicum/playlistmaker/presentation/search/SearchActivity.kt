package com.practicum.playlistmaker.presentation.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.common.ErrorType
import com.practicum.playlistmaker.presentation.mappers.DomainToUiMapper
import com.practicum.playlistmaker.presentation.mappers.UiToDomainMapper
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import com.practicum.playlistmaker.presentation.player.PlayerActivity
import java.util.concurrent.Executors

class SearchActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var searchRecycler: RecyclerView
    private lateinit var historyRecycler: RecyclerView
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var placeholderEmpty: View
    private lateinit var placeholderError: View
    private lateinit var retryButton: View
    private lateinit var searchHistoryBlock: View
    private lateinit var clearHistoryButton: View
    private lateinit var progress: View
    private val bg = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private val searchRunnable = Runnable {
        val q = searchEditText.text.toString().trim()
        if (q.isNotEmpty()) search(q)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        val root = findViewById<View>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, sys.top, v.paddingRight, v.paddingBottom)
            insets
        }

        //findViewById
        toolbar = findViewById(R.id.search_toolbar)
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        searchRecycler = findViewById(R.id.searchRecyclerView)
        historyRecycler = findViewById(R.id.historyRecycler)
        searchHistoryBlock = findViewById(R.id.search_history)
        placeholderEmpty = findViewById(R.id.not_result_placeholder)
        placeholderError = findViewById(R.id.network_error_placeholder)
        retryButton = placeholderError.findViewById(R.id.retrySearchButton)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progress = findViewById(R.id.searchProgressBar)

        //Адаптеры
        searchAdapter = TrackAdapter(emptyList()) { ui ->
            if (clickDebounce()) {
                addToHistory(ui)
                startActivity(PlayerActivity.createIntent(this, ui))
            }
        }
        historyAdapter = TrackAdapter(emptyList()) { ui ->
            if (clickDebounce()) {
                startActivity(PlayerActivity.createIntent(this, ui))
            }
        }
        searchRecycler.adapter = searchAdapter
        historyRecycler.adapter = historyAdapter
        loadHistory()

        //Действия при клике на кнопку "Назад" внутри раздела "Поиск"
        toolbar.setNavigationOnClickListener { finish() }

        //Работа поля поиска
        //Дейтсвие при клике на кнопку "Х" внутри поля поиска
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            handler.removeCallbacks(searchRunnable)
            searchEditText.requestFocus()
            loadHistory()
        }

        //Действие при клике на кнопку "Обновить" на networkErrorPlaceholder
        retryButton.setOnClickListener {
            val q = searchEditText.text?.toString()?.trim().orEmpty()
            if (q.isNotEmpty()) search(q)
        }

        //Действия с текстом внутри поля поиска
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                handler.removeCallbacks(searchRunnable)
                if (s.isNullOrBlank()) {
                    loadHistory()
                } else {
                    searchHistoryBlock.visibility = View.GONE
                    handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) loadHistory()
            else searchHistoryBlock.visibility = View.GONE
        }
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handler.removeCallbacks(searchRunnable)
                val q = searchEditText.text.toString().trim()
                if (q.isNotEmpty()) search(q)
                hideKeyboard()
                searchEditText.clearFocus()
                true
            } else false
        }

        //Действие при клике на кнопку "Очистить историю"
        clearHistoryButton.setOnClickListener {
            clearHistory()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }


    private fun search(q: String) {
        renderLoading()

        Creator.searchTracksInteractor.execute(q) { result ->
            runOnUiThread {
                if (result.isSuccess) {
                    val domain = result.data.orEmpty()
                    val ui = domain.map { DomainToUiMapper.trackToUi(it) }
                    if (ui.isEmpty()) {
                        renderEmpty()
                    } else {
                        renderContent(ui)
                    }
                } else {
                    when (result.error) {
                        ErrorType.EMPTY -> renderEmpty()
                        else -> renderError()
                    }
                }
            }
        }
    }

    private fun loadHistory() {
        bg.execute {
            val domain = Creator.getHistoryInteractor.execute()
            val ui = DomainToUiMapper.listToUi(domain)
            main.post { renderHistory(ui) }
        }
    }

    private fun addToHistory(ui: TrackUiDto) {
        bg.execute {
            val track = UiToDomainMapper.trackToDomain(ui)
            Creator.addToHistoryInteractor.execute(track)
        }
    }

    private fun clearHistory() {
        bg.execute {
            Creator.clearHistoryInteractor.execute()
            main.post { renderHistory(emptyList()) }
        }
    }

    //Методы рендеры
    private fun renderHistory(tracks: List<TrackUiDto>) {
        if (searchEditText.hasFocus() && searchEditText.text.isNullOrBlank() && tracks.isNotEmpty()) {
            historyRecycler.visibility = View.VISIBLE
            searchRecycler.visibility = View.GONE
            placeholderEmpty.visibility = View.GONE
            placeholderError.visibility = View.GONE
            progress.visibility = View.GONE
            searchHistoryBlock.visibility = View.VISIBLE
            historyAdapter.submitList(tracks)
        } else {
            searchHistoryBlock.visibility = View.GONE
        }
    }

    private fun renderLoading() {
        progress.visibility = View.VISIBLE
        searchRecycler.visibility = View.GONE
        searchHistoryBlock.visibility = View.GONE
        placeholderEmpty.visibility = View.GONE
        placeholderError.visibility = View.GONE
    }

    private fun renderContent(tracks: List<TrackUiDto>) {
        progress.visibility = View.GONE
        placeholderError.visibility = View.GONE
        searchHistoryBlock.visibility = View.GONE
        placeholderEmpty.visibility = View.GONE
        searchRecycler.visibility = View.VISIBLE
        searchAdapter.submitList(tracks)
    }

    private fun renderEmpty() {
        progress.visibility = View.GONE
        searchRecycler.visibility = View.GONE
        searchHistoryBlock.visibility = View.GONE
        placeholderError.visibility = View.GONE
        placeholderEmpty.visibility = View.VISIBLE
        searchAdapter.submitList(emptyList())
    }

    private fun renderError() {
        progress.visibility = View.GONE
        searchRecycler.visibility = View.GONE
        searchHistoryBlock.visibility = View.GONE
        placeholderEmpty.visibility = View.GONE
        placeholderError.visibility = View.VISIBLE
    }

    //Методы утилиты
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}