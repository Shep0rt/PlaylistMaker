package com.practicum.playlistmaker.presentation.media.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.interactor.playlist.GetPlaylistInteractor
import com.practicum.playlistmaker.domain.interactor.playlist.GetPlaylistTracksInteractor
import com.practicum.playlistmaker.domain.interactor.playlist.RemovePlaylistInteractor
import com.practicum.playlistmaker.domain.interactor.playlist.RemoveTrackFromPlaylistInteractor
import com.practicum.playlistmaker.presentation.mappers.DomainToUiMapper
import com.practicum.playlistmaker.presentation.models.PlaylistDetailsUiDto
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import java.util.concurrent.TimeUnit

class PlaylistViewModel(
    private val playlistId: Long,
    private val getPlaylistInteractor: GetPlaylistInteractor,
    private val getPlaylistTracksInteractor: GetPlaylistTracksInteractor,
    private val removeTrackFromPlaylistInteractor: RemoveTrackFromPlaylistInteractor,
    private val removePlaylistInteractor: RemovePlaylistInteractor
) : ViewModel() {

    private val state = MutableLiveData<PlaylistUiState>()
    val playlistState: LiveData<PlaylistUiState> = state

    private val tracksState = MutableLiveData<List<TrackUiDto>>()
    val playlistTracksState: LiveData<List<TrackUiDto>> = tracksState

    private val playlistRemoved = MutableLiveData<Boolean>()
    val playlistRemovedState: LiveData<Boolean> = playlistRemoved

    init {
        loadPlaylist()
    }

    private fun loadPlaylist() {
        state.value = PlaylistUiState.Loading
        viewModelScope.launch {
            getPlaylistInteractor.execute(playlistId)
                .combine(getPlaylistTracksInteractor.execute(playlistId)) { playlist, tracks ->
                    playlist to tracks
                }
                .collect { (playlist, tracks) ->
                    if (playlist == null) {
                        state.postValue(PlaylistUiState.NotFound)
                        return@collect
                    }
                    val durationSum = tracks.sumOf { it.trackTime }
                    val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationSum).toInt()

                    state.postValue(
                        PlaylistUiState.Content(
                            PlaylistDetailsUiDto(
                                id = playlist.id,
                                name = playlist.name,
                                description = playlist.description,
                                coverPath = playlist.coverPath,
                                durationMinutes = durationMinutes,
                                trackCount = tracks.size
                            )
                        )
                    )

                    tracksState.postValue(DomainToUiMapper.listToUi(tracks))
                }
        }
    }

    fun removeTrack(trackId: Long) {
        viewModelScope.launch {
            removeTrackFromPlaylistInteractor.execute(playlistId, trackId)
        }
    }

    fun removePlaylist() {
        viewModelScope.launch {
            removePlaylistInteractor.execute(playlistId)
            playlistRemoved.postValue(true)
        }
    }

    fun onPlaylistRemovedHandled() {
        playlistRemoved.value = false
    }
}
