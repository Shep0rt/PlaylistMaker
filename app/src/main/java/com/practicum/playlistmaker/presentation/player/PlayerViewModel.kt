package com.practicum.playlistmaker.presentation.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.presentation.models.OptionalField
import com.practicum.playlistmaker.presentation.models.PlayerUiState
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val track: TrackUiDto, private val mediaPlayer: MediaPlayer) : ViewModel() {

    private val state = MutableLiveData(
        PlayerUiState(
            playerState = PlayerState.Default,
            progress = PlayerConstants.DEFAULT_POSITION_TRACK
        )
    )
    val uiState: LiveData<PlayerUiState> = state

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = object : Runnable {
        override fun run() {
            val currentState = state.value ?: return
            if (currentState.playerState != PlayerState.Playing) return

            val formatted = SimpleDateFormat("m:ss", Locale.getDefault())
                .format(mediaPlayer.currentPosition)
            setProgress(formatted)

            handler.postDelayed(this, PlayerConstants.DELAY_REFRESH_DURATION_TRACK)
        }
    }

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        resetTimer()
    }

    fun onPlayButtonClicked() {
        when(state.value?.playerState) {
            PlayerState.Playing -> pausePlayer()
            PlayerState.Prepared, PlayerState.Paused -> startPlayer()
            PlayerState.Default -> {}
            null -> {}
        }
    }

    fun onPause() {
        pausePlayer()
    }

    private fun updateState(transform: (PlayerUiState) -> PlayerUiState) {
        val current = state.value ?: PlayerUiState(
            playerState = PlayerState.Default,
            progress = PlayerConstants.DEFAULT_POSITION_TRACK
        )
        state.postValue(transform(current))
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
            pauseTimer()
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
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        setPlayerState(PlayerState.Paused)
    }

    private fun startTimerUpdate() {
        handler.post(timerRunnable)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        setProgress(PlayerConstants.DEFAULT_POSITION_TRACK)
    }
}