package com.practicum.playlistmaker.presentation.player

import android.media.MediaPlayer
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: TrackUiDto,
    private val mediaPlayer: MediaPlayer,
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

    private var timerJob: Job? = null

    init {
        preparePlayer()
        checkFavoriteStatus()
        observePlaylists()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        timerJob?.cancel()
    }

    fun onPlayButtonClicked() {
        when (state.value?.playerState) {
            PlayerState.Playing -> pausePlayer()
            PlayerState.Prepared, PlayerState.Paused -> startPlayer()
            else -> {}
        }
    }

    fun onPause() {
        pausePlayer()
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

    private fun setPlayerState(state: PlayerState) {
        updateState { it.copy(playerState = state) }
    }

    private fun setProgress(progress: String) {
        updateState { it.copy(progress = progress) }
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

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            setPlayerState(PlayerState.Prepared)
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            mediaPlayer.seekTo(0)
            updateState {
                it.copy(
                    playerState = PlayerState.Prepared,
                    progress = PlayerConstants.DEFAULT_POSITION_TRACK
                )
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        setPlayerState(PlayerState.Playing)
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        setPlayerState(PlayerState.Paused)
        timerJob?.cancel()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (state.value?.playerState == PlayerState.Playing) {
                delay(PlayerConstants.DELAY_REFRESH_DURATION_TRACK)
                setProgress(formatTime(mediaPlayer.currentPosition))
            }
        }
    }

    private fun formatTime(time: Int): String {
        return SimpleDateFormat("m:ss", Locale.getDefault()).format(time)
    }
}