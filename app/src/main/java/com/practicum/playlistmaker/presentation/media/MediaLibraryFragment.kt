package com.practicum.playlistmaker.presentation.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.presentation.media.favorite.FavoriteTracksState
import com.practicum.playlistmaker.presentation.media.favorite.FavoriteTracksViewModel
import com.practicum.playlistmaker.presentation.media.playlist.PlaylistsState
import com.practicum.playlistmaker.presentation.media.playlist.PlaylistsViewModel
import com.practicum.playlistmaker.presentation.media.ui.MediaLibraryScreen
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerScreenTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment() {

    private val favoriteTracksViewModel: FavoriteTracksViewModel by viewModel()
    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val favoriteState by favoriteTracksViewModel.favoriteTracksState.observeAsState(
                    initial = FavoriteTracksState.Loading
                )
                val playlistsState by playlistsViewModel.playlistsState.observeAsState(
                    initial = PlaylistsState.Loading
                )

                PlaylistMakerTheme(screenTheme = PlaylistMakerScreenTheme.MediaLibrary) {
                    MediaLibraryScreen(
                        favoriteTracksState = favoriteState,
                        playlistsState = playlistsState,
                        onTrackClick = { track ->
                            val action = MediaLibraryFragmentDirections
                                .actionMediaLibraryFragmentToPlayerFragment(track)
                            findNavController().navigate(action)
                        },
                        onCreatePlaylistClick = {
                            val action = MediaLibraryFragmentDirections
                                .actionMediaLibraryFragmentToCreatePlaylistFragment(null)
                            findNavController().navigate(action)
                        },
                        onPlaylistClick = { playlist ->
                            val action = MediaLibraryFragmentDirections
                                .actionMediaLibraryFragmentToPlaylistFragment(playlist.id)
                            findNavController().navigate(action)
                        },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        favoriteTracksViewModel.fillData()
    }
}