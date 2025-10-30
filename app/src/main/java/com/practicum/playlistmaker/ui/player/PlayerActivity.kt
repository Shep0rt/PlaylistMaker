package com.practicum.playlistmaker.ui.player

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.model.Track
import android.os.Handler

class PlayerActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var cover: ImageView
    private lateinit var titleTrack: TextView
    private lateinit var artist: TextView
    private lateinit var addPlaylist: ImageButton
    private lateinit var play: ImageButton
    private lateinit var like: ImageButton
    private lateinit var durationTrack: TextView

    private  lateinit var tvDurationLabel: TextView
    private lateinit var tvDurationValue: TextView
    private lateinit var tvAlbumLabel: TextView
    private lateinit var tvAlbumValue: TextView
    private lateinit var tvYearLabel: TextView
    private lateinit var tvYearValue: TextView
    private lateinit var tvGenreLabel: TextView
    private lateinit var tvGenreValue: TextView
    private lateinit var tvCountryLabel: TextView
    private lateinit var tvCountryValue: TextView
    private lateinit var mediaPlayer: MediaPlayer
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private val trackDurationRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                durationTrack.text = formatDuration(mediaPlayer.currentPosition.toLong())
                handler.postDelayed(this, DELAY_REFRESH_DURATION_TRACK)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.playerScroll)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar = findViewById(R.id.toolbarPlayer)
        cover = findViewById(R.id.cover)
        titleTrack = findViewById(R.id.titleTrack)
        artist = findViewById(R.id.artist)
        addPlaylist = findViewById(R.id.addPlaylist)
        play = findViewById(R.id.play)
        like = findViewById(R.id.like)
        durationTrack = findViewById(R.id.durationTrack)

        tvDurationLabel = findViewById(R.id.descriptionDurationText)
        tvDurationValue = findViewById(R.id.descriptionDurationValue)
        tvAlbumLabel = findViewById(R.id.descriptionAlbumText)
        tvAlbumValue = findViewById(R.id.descriptionAlbumValue)
        tvYearLabel = findViewById(R.id.descriptionYearText)
        tvYearValue = findViewById(R.id.descriptionYearValue)
        tvGenreLabel = findViewById(R.id.descriptionGenreText)
        tvGenreValue = findViewById(R.id.descriptionGenreValue)
        tvCountryLabel = findViewById(R.id.descriptionCountryText)
        tvCountryValue = findViewById(R.id.descriptionCountryValue)

        mediaPlayer = MediaPlayer()

        //Получаем трек и присваиваем значения во Views
        val track = getParcelableExtraCompat<Track>(EXTRA_TRACK)
        if (track == null || track.previewUrl.isNullOrBlank()) {
            finish()
            return
        }
        bindTrack(track)
        preparePlayer(track.previewUrl)

        //Действие при нажатии кнопки назад на toolbar
        toolbar.setNavigationOnClickListener { finish() }

        //Действие при нажатии на кнопку play
        play.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdatingTime()
        mediaPlayer.release()
    }

    private fun preparePlayer(url: String) {
        play.isEnabled = false
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play.setImageResource(R.drawable.ic_button_play100)
            playerState = STATE_PREPARED
            stopUpdatingTime()
            durationTrack.text = DEFAULT_DURATION_TRACK
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.ic_button_pause100)
        playerState = STATE_PLAYING
        startUpdatingTime()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.ic_button_play100)
        playerState = STATE_PAUSED
        stopUpdatingTime()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startUpdatingTime() {
        handler.post(trackDurationRunnable)
    }

    private fun stopUpdatingTime() {
        handler.removeCallbacks(trackDurationRunnable)
    }

    private fun bindTrack(track: Track) {
        titleTrack.text = track.trackName
        artist.text = track.artistName
        tvDurationValue.text = formatDuration(track.trackTime)

        val hiResUrl = toHighResArtwork(track.artworkUrl100)
        Glide.with(this)
            .load(hiResUrl)
            .transform(
                FitCenter(),
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        this.resources.displayMetrics
                    ).toInt()
                )
            )
            .placeholder(R.drawable.ic_not_cover_placeholder312)
            .error(R.drawable.ic_not_cover_placeholder312)
            .fallback(R.drawable.ic_not_cover_placeholder312)
            .into(cover)

        bindOptionalPair(tvAlbumLabel, tvAlbumValue, track.collectionName)
        bindOptionalPair(tvYearLabel, tvYearValue, isoToYear(track.releaseDate))
        bindOptionalPair(tvGenreLabel, tvGenreValue, track.primaryGenreName)
        bindOptionalPair(tvCountryLabel, tvCountryValue, track.country)
    }

    private fun bindOptionalPair(
        label: TextView,
        value: TextView,
        text: String?
    ) {
        val has = !text.isNullOrBlank()
        label.isVisible = has
        value.isVisible = has
        if (has) value.text = text
    }

    private fun isoToYear(iso: String?): String? =
        iso?.takeIf { it.length >= 4 }?.substring(0, 4)

    private fun formatDuration(ms: Long): String {
        val totalSec = (ms / 1000).toInt()
        val m = totalSec / 60
        val s = totalSec % 60
        return "%d:%02d".format(m, s)
    }

    private fun toHighResArtwork(url: String?): String? {
        if (url.isNullOrBlank()) return null
        return url.replace(Regex("/\\d+x\\d+bb\\.jpg$"), "/512x512bb.jpg")
    }

    companion object {
        private const val EXTRA_TRACK = "extra_track"

        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY_REFRESH_DURATION_TRACK = 300L
        private const val DEFAULT_DURATION_TRACK = "0:00"

        fun createIntent(context: Context, track: Track): Intent {
            return Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_TRACK, track)
            }
        }
    }
}

inline fun <reified T : android.os.Parcelable> AppCompatActivity.getParcelableExtraCompat(
    key: String
): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent.getParcelableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        intent.getParcelableExtra(key)
    }
}

