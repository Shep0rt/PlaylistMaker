package com.practicum.playlistmaker.presentation.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import com.practicum.playlistmaker.presentation.player.PlayerActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyInsets()
        initViewModel()
        setupAdapters()
        setupListeners()
    }

    private fun initViewModel() {
        val factory = SearchViewModelFactory(
            searchTracksInteractor = Creator.searchTracksInteractor,
            getHistoryInteractor = Creator.getHistoryInteractor,
            saveToHistoryInteractor = Creator.addToHistoryInteractor,
            clearHistoryInteractor = Creator.clearHistoryInteractor
        )

        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

        viewModel.searchState.observe(this) { state -> renderState(state) }
    }

    private fun setupAdapters() {
        searchAdapter = TrackAdapter(emptyList()) { ui ->
            viewModel.onTrackClicked(ui)
            startActivity(PlayerActivity.createIntent(this, ui))
        }

        historyAdapter = TrackAdapter(emptyList()) { ui ->
            startActivity(PlayerActivity.createIntent(this, ui))
        }

        binding.searchRecyclerView.adapter = searchAdapter
        binding.searchHistory.historyRecycler.adapter = historyAdapter
    }

    private fun setupListeners() = with(binding) {
        //Действия при клике на кнопку "Назад" внутри раздела "Поиск"
        searchToolbar.setNavigationOnClickListener { finish() }

        //Работа поля поиска
        //Дейтсвие при клике на кнопку "Х" внутри поля поиска
        clearButton.setOnClickListener {
            binding.searchEditText.text.clear()
        }
        //Действие при клике на кнопку "Обновить" на networkErrorPlaceholder
        networkErrorPlaceholder.retrySearchButton.setOnClickListener {
            val q = searchEditText.text.toString().trim()
            viewModel.retry(q)
        }
        //Действие при клике на кнопку "Очистить историю"
        searchHistory.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }
        //Действия с текстом внутри поля поиска
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, b: Int, c: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                viewModel.onQueryChanged(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                viewModel.loadHistory()
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val q = searchEditText.text.toString().trim()
                viewModel.retry(q)
                hideKeyboard()
                true
            } else false
        }
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, sys.top, v.paddingRight, v.paddingBottom)
            insets
        }
    }

    //Методы рендеры

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.Idle -> showIdle()
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Empty -> showEmpty()
            is SearchState.Error -> showError()
            is SearchState.History -> showHistory(state.tracks)
        }
    }

    private fun showIdle() = with(binding) {
        searchProgressBar.isVisible = false
        searchRecyclerView.isVisible = false
        searchHistory.historyView.isVisible = false
        notResultPlaceholder.searchNotResultPlaceholder.isVisible = false
        networkErrorPlaceholder.searchNetworkError.isVisible = false
    }

    private fun showLoading() = with(binding) {
        searchProgressBar.isVisible = true
        searchRecyclerView.isVisible = false
        searchHistory.historyView.isVisible = false
        notResultPlaceholder.searchNotResultPlaceholder.isVisible = false
        networkErrorPlaceholder.searchNetworkError.isVisible = false
    }

    private fun showContent(tracks: List<TrackUiDto>) = with(binding) {
        searchProgressBar.isVisible = false
        networkErrorPlaceholder.searchNetworkError.isVisible = false
        notResultPlaceholder.searchNotResultPlaceholder.isVisible  = false
        searchHistory.historyView.isVisible = false
        searchRecyclerView.isVisible = true
        searchAdapter.submitList(tracks)
    }

    private fun showHistory(tracks: List<TrackUiDto>) = with(binding) {
        if (tracks.isEmpty()) {
            showIdle()
            return
        }

        searchProgressBar.isVisible = false
        searchRecyclerView.isVisible = false
        networkErrorPlaceholder.searchNetworkError.isVisible = false
        notResultPlaceholder.searchNotResultPlaceholder.isVisible = false
        historyAdapter.submitList(tracks)
        searchHistory.historyView.isVisible = true
        searchHistory.historyRecycler.isVisible = true
    }

    private fun showEmpty() = with(binding) {
        searchProgressBar.isVisible = false
        searchRecyclerView.isVisible = false
        searchHistory.historyView.isVisible = false
        networkErrorPlaceholder.searchNetworkError.isVisible = false
        notResultPlaceholder.searchNotResultPlaceholder.isVisible = true
    }

    private fun showError() = with(binding) {
        searchProgressBar.isVisible = false
        searchRecyclerView.isVisible = false
        searchHistory.historyView.isVisible = false
        notResultPlaceholder.searchNotResultPlaceholder.isVisible = false
        networkErrorPlaceholder.searchNetworkError.isVisible = true
    }

    //Методы утилиты
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }
}