package com.practicum.playlistmaker.presentation.search

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import com.practicum.playlistmaker.util.InternetConnectionReceiver
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var isInternetReceiverRegistered = false
    private val internetReceiver = InternetConnectionReceiver { context ->
        showNoInternetToast(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupListeners()

        viewModel.searchState.observe(viewLifecycleOwner) { state -> renderState(state) }
    }

    override fun onResume() {
        super.onResume()
        internetReceiver.resetInitialState(requireContext())
        registerInternetReceiver()
    }

    override fun onPause() {
        unregisterInternetReceiver()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openPlayer(track: TrackUiDto) {
        val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
        findNavController().navigate(action)
    }

    private fun setupAdapters() {
        searchAdapter = TrackAdapter(emptyList()) { track ->
            viewModel.onTrackClicked(track)
            openPlayer(track)
        }

        historyAdapter = TrackAdapter(emptyList()) { track ->
            openPlayer(track)
        }

        binding.searchRecyclerView.adapter = searchAdapter
        binding.searchHistory.historyRecycler.adapter = historyAdapter
    }

    private fun setupListeners() = with(binding) {
        clearButton.setOnClickListener {
            binding.searchEditText.text.clear()
            hideKeyboard()
        }

        networkErrorPlaceholder.retrySearchButton.setOnClickListener {
            val q = searchEditText.text.toString().trim()
            viewModel.retry(q)
        }

        searchHistory.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

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
        notResultPlaceholder.searchNotResultPlaceholder.isVisible = false
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

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun registerInternetReceiver() {
        if (isInternetReceiverRegistered) return
        ContextCompat.registerReceiver(
            requireContext(),
            internetReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        isInternetReceiverRegistered = true
    }

    private fun unregisterInternetReceiver() {
        if (!isInternetReceiverRegistered) return
        try {
            requireContext().unregisterReceiver(internetReceiver)
        } catch (_: IllegalArgumentException) {
        } finally {
            isInternetReceiverRegistered = false
        }
    }

    private fun showNoInternetToast(context: Context) {
        Toast.makeText(
            context,
            context.getString(R.string.no_internet_connection),
            Toast.LENGTH_SHORT
        ).show()
    }
}