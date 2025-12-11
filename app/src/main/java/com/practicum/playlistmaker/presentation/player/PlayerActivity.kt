package com.practicum.playlistmaker.presentation.player

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import android.os.Parcelable
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: TrackUiDto
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyInsets()
        //Получаем трек и присваиваем значения во Views
        track = getParcelableExtraCompat(EXTRA_TRACK) ?: run {
            finish()
            return
        }
        bindTrack(track)
        setupObservers()
        setupListeners()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun setupObservers() {
        viewModel.observePlayerState().observe(this) { state ->
            binding.play.isEnabled = state != PlayerState.Default
            binding.play.setImageResource(
                if (state == PlayerState.Playing)
                    R.drawable.ic_button_pause100
                else
                    R.drawable.ic_button_play100
            )
        }

        viewModel.observeProgressTime().observe(this) {
            binding.durationTrack.text = it
        }
    }

    private fun setupListeners() {
        binding.toolbarPlayer.setNavigationOnClickListener { finish() }

        binding.play.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.playerScroll) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }
    }

    private fun bindTrack(track: TrackUiDto) = with(binding) {
        //заголовки
        titleTrack.text = track.trackName
        artist.text = track.artistName
        descriptionDurationValue.text = track.trackTime

        //обложка
        val hiResUrl = viewModel.toHighResArtwork(track.artworkUrl100)

        Glide.with(this@PlayerActivity)
            .load(hiResUrl)
            .transform(
                FitCenter(),
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        resources.displayMetrics
                    ).toInt()
                )
            )
            .placeholder(R.drawable.ic_not_cover_placeholder312)
            .error(R.drawable.ic_not_cover_placeholder312)
            .fallback(R.drawable.ic_not_cover_placeholder312)
            .into(cover)

        //альбом
        val albumField = viewModel.createOptionalField(track.collectionName)
        descriptionAlbumText.isVisible = albumField.isVisible
        descriptionAlbumValue.isVisible = albumField.isVisible
        descriptionAlbumValue.text = albumField.text

        //год
        val yearField = viewModel.createOptionalField(viewModel.isoToYear(track.releaseDate))
        descriptionYearText.isVisible = yearField.isVisible
        descriptionYearValue.isVisible = yearField.isVisible
        descriptionYearValue.text = yearField.text

        //жанр
        val genreField = viewModel.createOptionalField(track.primaryGenreName)
        descriptionGenreText.isVisible = genreField.isVisible
        descriptionGenreValue.isVisible = genreField.isVisible
        descriptionGenreValue.text = genreField.text

        //страна
        val countryField = viewModel.createOptionalField(track.country)
        descriptionCountryText.isVisible = countryField.isVisible
        descriptionCountryValue.isVisible = countryField.isVisible
        descriptionCountryValue.text = countryField.text
    }

    private inline fun <reified T : Parcelable> AppCompatActivity.getParcelableExtraCompat(
        key: String
    ): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(key)
        }
    }

    companion object {
        private const val EXTRA_TRACK = "extra_track"

        fun createIntent(context: Context, track: TrackUiDto): Intent {
            return Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_TRACK, track)
            }
        }
    }
}