package com.practicum.playlistmaker.presentation.player

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private val args by navArgs<PlayerFragmentArgs>()
    private val track: TrackUiDto by lazy { args.track }
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindTrack(track)
        setupObservers()
        setupListeners()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.play.isEnabled = state.playerState != PlayerState.Default
            binding.play.setImageResource(
                if (state.playerState == PlayerState.Playing)
                    R.drawable.ic_button_pause100
                else
                    R.drawable.ic_button_play100
            )
            binding.durationTrack.text = state.progress
        }
    }

    private fun setupListeners() {
        binding.toolbarPlayer.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.play.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    private fun bindTrack(track: TrackUiDto) = with(binding) {
        titleTrack.text = track.trackName
        artist.text = track.artistName
        descriptionDurationValue.text = track.trackTime

        val hiResUrl = viewModel.toHighResArtwork(track.artworkUrl100)

        Glide.with(this@PlayerFragment)
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

        val albumField = viewModel.createOptionalField(track.collectionName)
        descriptionAlbumText.isVisible = albumField.isVisible
        descriptionAlbumValue.isVisible = albumField.isVisible
        descriptionAlbumValue.text = albumField.text

        val yearField = viewModel.createOptionalField(viewModel.isoToYear(track.releaseDate))
        descriptionYearText.isVisible = yearField.isVisible
        descriptionYearValue.isVisible = yearField.isVisible
        descriptionYearValue.text = yearField.text

        val genreField = viewModel.createOptionalField(track.primaryGenreName)
        descriptionGenreText.isVisible = genreField.isVisible
        descriptionGenreValue.isVisible = genreField.isVisible
        descriptionGenreValue.text = genreField.text

        val countryField = viewModel.createOptionalField(track.country)
        descriptionCountryText.isVisible = countryField.isVisible
        descriptionCountryValue.isVisible = countryField.isVisible
        descriptionCountryValue.text = countryField.text
    }
}
