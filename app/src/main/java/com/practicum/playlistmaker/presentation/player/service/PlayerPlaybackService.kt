package com.practicum.playlistmaker.presentation.player.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.player.PlayerConstants
import com.practicum.playlistmaker.presentation.player.PlayerState
import com.practicum.playlistmaker.presentation.root.RootActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerPlaybackService : Service(), PlayerServiceApi {

    inner class PlayerPlaybackBinder : Binder() {
        fun getService(): PlayerPlaybackService = this@PlayerPlaybackService
    }

    companion object {
        const val EXTRA_PREVIEW_URL = "extra_preview_url"
        const val EXTRA_ARTIST_NAME = "extra_artist_name"
        const val EXTRA_TRACK_NAME = "extra_track_name"

        private const val NOTIFICATION_CHANNEL_ID = "playlist_maker_playback"
        private const val NOTIFICATION_ID = 1001
    }

    private val binder = PlayerPlaybackBinder()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var timerJob: Job? = null

    private var mediaPlayer: MediaPlayer? = null
    private var previewUrl: String? = null
    private var artistName: String = ""
    private var trackName: String = ""

    private val _state = MutableStateFlow(PlayerPlaybackState())
    private val state: StateFlow<PlayerPlaybackState> = _state.asStateFlow()

    private var isInForegroundMode = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        previewUrl = intent?.getStringExtra(EXTRA_PREVIEW_URL)
        artistName = intent?.getStringExtra(EXTRA_ARTIST_NAME).orEmpty()
        trackName = intent?.getStringExtra(EXTRA_TRACK_NAME).orEmpty()
        preparePlayer()
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopForegroundMode()
        stopAndRelease()
        stopSelf()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        stopForegroundMode()
        stopAndRelease()
        serviceScope.coroutineContext.cancel()
        super.onDestroy()
    }

    override fun play() {
        val player = mediaPlayer ?: return
        if (_state.value.playerState == PlayerState.Default) return
        if (!player.isPlaying) {
            player.start()
        }
        setPlayerState(PlayerState.Playing)
        startTimer()
        if (isInForegroundMode) {
            updateForegroundNotification()
        }
    }

    override fun pause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            player.pause()
        }
        timerJob?.cancel()
        setPlayerState(PlayerState.Paused)
        stopForegroundMode()
    }

    override fun getPlayerState(): PlayerState = _state.value.playerState

    override fun playbackState(): StateFlow<PlayerPlaybackState> = state

    override fun startForegroundMode() {
        if (isInForegroundMode) return
        if (_state.value.playerState != PlayerState.Playing) return

        try {
            isInForegroundMode = true
            val notification = buildNotification()

            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } catch (_: SecurityException) {
            isInForegroundMode = false
        } catch (_: IllegalStateException) {
            isInForegroundMode = false
        } catch (_: RuntimeException) {
            isInForegroundMode = false
        }
    }

    override fun stopForegroundMode() {
        if (!isInForegroundMode) return
        isInForegroundMode = false
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun preparePlayer() {
        stopAndRelease()

        val url = previewUrl
        if (url.isNullOrBlank()) {
            _state.value = PlayerPlaybackState(playerState = PlayerState.Default)
            return
        }

        val player = MediaPlayer()
        mediaPlayer = player
        _state.value = PlayerPlaybackState(playerState = PlayerState.Default)

        player.setDataSource(url)
        player.setOnPreparedListener {
            _state.value = _state.value.copy(playerState = PlayerState.Prepared)
        }
        player.setOnCompletionListener {
            timerJob?.cancel()
            stopForegroundMode()
            it.seekTo(0)
            _state.value = PlayerPlaybackState(
                playerState = PlayerState.Prepared,
                progress = PlayerConstants.DEFAULT_POSITION_TRACK
            )
        }
        player.prepareAsync()
    }

    private fun stopAndRelease() {
        timerJob?.cancel()
        timerJob = null

        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null

        _state.value = PlayerPlaybackState(playerState = PlayerState.Default)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (_state.value.playerState == PlayerState.Playing) {
                delay(PlayerConstants.DELAY_REFRESH_DURATION_TRACK)
                val progress = mediaPlayer?.currentPosition?.let(::formatTime)
                    ?: PlayerConstants.DEFAULT_POSITION_TRACK
                _state.value = _state.value.copy(progress = progress)
            }
        }
    }

    private fun setPlayerState(newState: PlayerState) {
        _state.value = _state.value.copy(playerState = newState)
    }

    private fun formatTime(timeMs: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMs)
    }

    private fun createNotificationChannel() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_LOW
        )
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification() = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_media_library24)
        .setContentTitle(getString(R.string.app_name))
        .setContentText(notificationText())
        .setContentIntent(notificationContentIntent())
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .build()

    private fun updateForegroundNotification() {
        if (!isInForegroundMode) return
        try {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(NOTIFICATION_ID, buildNotification())
        } catch (_: SecurityException) {
            stopForegroundMode()
        }
    }

    private fun notificationText(): String {
        val trackText = listOf(artistName, trackName).filter { it.isNotBlank() }.joinToString(" - ")
        return trackText.ifBlank { getString(R.string.app_name) }
    }

    private fun notificationContentIntent(): PendingIntent {
        val intent = Intent(this, RootActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getActivity(this, 0, intent, flags)
    }
}
