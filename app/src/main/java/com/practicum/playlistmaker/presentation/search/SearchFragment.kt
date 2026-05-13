package com.practicum.playlistmaker.presentation.search

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import com.practicum.playlistmaker.presentation.search.ui.SearchScreen
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerScreenTheme
import com.practicum.playlistmaker.util.InternetConnectionReceiver
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    private var isInternetReceiverRegistered = false
    private val internetReceiver = InternetConnectionReceiver { context ->
        showNoInternetToast(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by viewModel.searchState.observeAsState(initial = SearchState.Idle)

                PlaylistMakerTheme(screenTheme = PlaylistMakerScreenTheme.Search) {
                    SearchScreen(
                        state = state,
                        onQueryChanged = viewModel::onQueryChanged,
                        onSearchAction = viewModel::retry,
                        onClearHistory = viewModel::clearHistory,
                        onRetry = viewModel::retry,
                        onTrackClickFromSearch = { track ->
                            viewModel.onTrackClicked(track)
                            openPlayer(track)
                        },
                        onTrackClickFromHistory = ::openPlayer,
                        onQueryFieldFocusedAndEmpty = viewModel::loadHistory,
                    )
                }
            }
        }
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

    private fun openPlayer(track: TrackUiDto) {
        val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
        findNavController().navigate(action)
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