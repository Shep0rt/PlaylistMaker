package com.practicum.playlistmaker.presentation.media.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.interactor.playlist.GetPlaylistsInteractor
import com.practicum.playlistmaker.presentation.models.PlaylistUiDto
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val getPlaylistsInteractor: GetPlaylistsInteractor
) : ViewModel() {

    private val state = MutableLiveData<PlaylistsState>()
    val playlistsState: LiveData<PlaylistsState> = state

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        state.value = PlaylistsState.Loading
        viewModelScope.launch {
            getPlaylistsInteractor.execute().collect { playlists ->
                if (playlists.isEmpty()) {
                    state.postValue(PlaylistsState.Empty)
                } else {
                    val uiPlaylists = playlists.map { playlist ->
                        PlaylistUiDto(
                            id = playlist.id,
                            name = playlist.name,
                            description = playlist.description,
                            coverPath = playlist.coverPath,
                            trackCount = playlist.trackCount
                        )
                    }
                    state.postValue(PlaylistsState.Content(uiPlaylists))
                }
            }
        }
    }
}