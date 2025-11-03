package com.practicum.playlistmaker.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.common.PlayerState
import com.practicum.playlistmaker.domain.repository.PlayerEvents
import com.practicum.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {

    private var currentState = PlayerState.DEFAULT
    private var events: PlayerEvents? = null

    override fun prepare(url: String): Boolean {
        release()
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)

        mediaPlayer.setOnPreparedListener {
            currentState = PlayerState.PREPARED
            events?.onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            currentState = PlayerState.PREPARED
            events?.onCompleted()
        }

        currentState = PlayerState.PREPARING
        mediaPlayer.prepareAsync()
        return true
    }

    override fun play() {
        mediaPlayer.start()
        currentState = PlayerState.PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        currentState = PlayerState.PAUSED
    }

    override fun stop() {
        mediaPlayer.stop()
        release()
        currentState = PlayerState.DEFAULT
    }

    override fun state(): PlayerState {
        return currentState
    }

    override fun currentPositionMs(): Int {
        return mediaPlayer.currentPosition
    }

    override fun durationMs(): Int {
        return mediaPlayer.duration
    }

    override fun setEventsListener(listener: PlayerEvents?) {
        events = listener
    }

    private fun release() {
        mediaPlayer.setOnPreparedListener(null)
        mediaPlayer.setOnCompletionListener(null)
    }
}