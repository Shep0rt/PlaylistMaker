package com.practicum.playlistmaker.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.interactor.favorite.AddTrackToFavoritesInteractor
import com.practicum.playlistmaker.domain.interactor.favorite.IsFavoriteTrackInteractor
import com.practicum.playlistmaker.domain.interactor.favorite.RemoveTrackFromFavoritesInteractor
import com.practicum.playlistmaker.domain.interactor.impl.playlist.AddToPlaylistResult
import com.practicum.playlistmaker.domain.interactor.playlist.AddTrackToPlaylistInteractor
import com.practicum.playlistmaker.domain.interactor.playlist.GetPlaylistsInteractor
import com.practicum.playlistmaker.presentation.mappers.UiToDomainMapper
import com.practicum.playlistmaker.presentation.models.OptionalField
import com.practicum.playlistmaker.presentation.models.PlayerUiState
import com.practicum.playlistmaker.presentation.models.PlaylistUiDto
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import com.practicum.playlistmaker.presentation.player.service.PlayerPlaybackState
import com.practicum.playlistmaker.presentation.player.service.PlayerServiceApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: TrackUiDto,
    private val addTrackToFavoritesInteractor: AddTrackToFavoritesInteractor,
    private val removeTrackFromFavoritesInteractor: RemoveTrackFromFavoritesInteractor,
    private val isFavoriteTrackInteractor: IsFavoriteTrackInteractor,
    private val getPlaylistsInteractor: GetPlaylistsInteractor,
    private val addTrackToPlaylistInteractor: AddTrackToPlaylistInteractor
) : ViewModel() {

    private val state = MutableLiveData(
        PlayerUiState(
            playerState = PlayerState.Default,
            progress = PlayerConstants.DEFAULT_POSITION_TRACK,
            isFavorite = track.isFavorite
        )
    )
    val uiState: LiveData<PlayerUiState> = state

    private val playlists = MutableLiveData<List<PlaylistUiDto>>(emptyList())
    val playlistsState: LiveData<List<PlaylistUiDto>> = playlists

    private val addToPlaylistState = MutableLiveData<AddToPlaylistUiState?>(null)
    val addToPlaylistResult: LiveData<AddToPlaylistUiState?> = addToPlaylistState

    private var playerService: PlayerServiceApi? = null
    private var playerStateJob: Job? = null

    init {
        checkFavoriteStatus()
        observePlaylists()
    }

    override fun onCleared() {
        super.onCleared()
        playerStateJob?.cancel()
    }

    fun onPlayButtonClicked() {
        val service = playerService ?: return
        when (service.getPlayerState()) {
            PlayerState.Playing -> service.pause()
            PlayerState.Prepared, PlayerState.Paused -> service.play()
            else -> {}
        }
    }

    fun onPlayerScreenStarted() {
        playerService?.stopForegroundMode()
    }

    fun onPlayerScreenStopped(canShowNotification: Boolean) {
        if (!canShowNotification) return
        val service = playerService ?: return
        if (service.getPlayerState() == PlayerState.Playing) {
            service.startForegroundMode()
        }
    }

    fun onFavoriteButtonClicked() {
        val currentState = state.value ?: return
        val newFavoriteStatus = !currentState.isFavorite
        
        viewModelScope.launch {
            val domainTrack = UiToDomainMapper.trackToDomain(track)
            if (newFavoriteStatus) {
                addTrackToFavoritesInteractor.execute(domainTrack)
            } else {
                removeTrackFromFavoritesInteractor.execute(domainTrack)
            }
            updateState { it.copy(isFavorite = newFavoriteStatus) }
        }
    }

    fun onAddToPlaylistClicked(playlist: PlaylistUiDto) {
        viewModelScope.launch {
            val domainTrack = UiToDomainMapper.trackToDomain(track)
            val result = addTrackToPlaylistInteractor.execute(domainTrack, playlist.id)
            addToPlaylistState.value = when (result) {
                AddToPlaylistResult.Added -> AddToPlaylistUiState.Added(playlist.name)
                AddToPlaylistResult.AlreadyExists -> AddToPlaylistUiState.AlreadyExists(playlist.name)
            }
        }
    }

    fun onAddToPlaylistMessageShown() {
        addToPlaylistState.value = null
    }

    private fun checkFavoriteStatus() {
        viewModelScope.launch {
            val isFavorite = isFavoriteTrackInteractor.execute(track.id)
            updateState { it.copy(isFavorite = isFavorite) }
        }
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            getPlaylistsInteractor.execute().collect { playlistsList ->
                playlists.value = playlistsList.map { playlist ->
                    PlaylistUiDto(
                        id = playlist.id,
                        name = playlist.name,
                        description = playlist.description,
                        coverPath = playlist.coverPath,
                        trackCount = playlist.trackCount
                    )
                }
            }
        }
    }

    private fun updateState(transform: (PlayerUiState) -> PlayerUiState) {
        val current = state.value ?: PlayerUiState(
            playerState = PlayerState.Default,
            progress = PlayerConstants.DEFAULT_POSITION_TRACK,
            isFavorite = track.isFavorite
        )
        state.value = transform(current)
    }

    fun isoToYear(iso: String?): String? =
        iso?.takeIf { it.length >= 4 }?.substring(0, 4)

    fun toHighResArtwork(url: String?): String? {
        if (url.isNullOrBlank()) return null
        return url.replace(Regex("/\\d+x\\d+bb\\.jpg$"), "/512x512bb.jpg")
    }

    fun createOptionalField(text: String?): OptionalField {
        val visible = !text.isNullOrBlank()
        return OptionalField(
            text = text?.takeIf { visible },
            isVisible = visible
        )
    }

    fun onServiceConnected(service: PlayerServiceApi) {
        playerService = service
        playerStateJob?.cancel()
        playerStateJob = viewModelScope.launch {
            service.playbackState().collectLatest(::onPlaybackStateChanged)
        }
        service.stopForegroundMode()
    }

    fun onServiceDisconnected() {
        playerStateJob?.cancel()
        playerStateJob = null
        playerService = null
    }

    private fun onPlaybackStateChanged(serviceState: PlayerPlaybackState) {
        updateState {
            it.copy(
                playerState = serviceState.playerState,
                progress = serviceState.progress
            )
        }
    }
}