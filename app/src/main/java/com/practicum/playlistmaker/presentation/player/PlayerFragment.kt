package com.practicum.playlistmaker.presentation.player

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import com.practicum.playlistmaker.presentation.player.service.PlayerPlaybackService
import com.practicum.playlistmaker.util.InternetConnectionReceiver
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<PlayerFragmentArgs>()
    private val track: TrackUiDto by lazy(LazyThreadSafetyMode.NONE) { args.track }
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val playlistsAdapter = PlaylistBottomSheetAdapter { playlist ->
        viewModel.onAddToPlaylistClicked(playlist)
    }

    private var isInternetReceiverRegistered = false
    private val internetReceiver = InternetConnectionReceiver { context ->
        showNoInternetToast(context)
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    private var isServiceBound = false
    private val playerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? PlayerPlaybackService.PlayerPlaybackBinder ?: return
            viewModel.onServiceConnected(binder.getService())
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            viewModel.onServiceDisconnected()
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            val binding = _binding ?: return
            val isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            binding.bottomSheetOverlay.isVisible = isVisible
            binding.bottomSheetOverlay.alpha = if (isVisible) 1f else 0f
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            val binding = _binding ?: return
            if (slideOffset > 0f) {
                binding.bottomSheetOverlay.isVisible = true
                binding.bottomSheetOverlay.alpha = slideOffset
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestNotificationPermissionIfNeeded()
        bindPlayerService()
        bindTrack(track)
        setupBottomSheet()
        setupObservers()
        setupListeners()
    }

    override fun onStart() {
        super.onStart()
        viewModel.onPlayerScreenStarted()
        if (::bottomSheetBehavior.isInitialized) {
            syncBottomSheetOverlay()
        }
    }

    override fun onResume() {
        super.onResume()
        internetReceiver.resetInitialState(requireContext())
        registerInternetReceiver()
    }

    override fun onPause() {
        unregisterInternetReceiver()
        super.onPause()
    }

    override fun onStop() {
        viewModel.onPlayerScreenStopped(canShowNotifications())
        super.onStop()
    }

    override fun onDestroyView() {
        unbindPlayerService()
        super.onDestroyView()
        if (::bottomSheetBehavior.isInitialized) {
            bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)
        }
        _binding = null
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.play.isEnabled = state.playerState != PlayerState.Default
            binding.play.setPlaying(state.playerState == PlayerState.Playing)
            binding.durationTrack.text = state.progress
            
            binding.like.setImageResource(
                if (state.isFavorite)
                    R.drawable.ic_like51_active
                else
                    R.drawable.ic_like51_inactive
            )
        }

        viewModel.playlistsState.observe(viewLifecycleOwner) { playlists ->
            playlistsAdapter.submitList(playlists)
        }

        viewModel.addToPlaylistResult.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe
            when (result) {
                is AddToPlaylistUiState.Added -> {
                    showToast(getString(R.string.track_added_to_playlist, result.playlistName))
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                is AddToPlaylistUiState.AlreadyExists -> {
                    showToast(getString(R.string.track_already_in_playlist, result.playlistName))
                }
            }
            viewModel.onAddToPlaylistMessageShown()
        }
    }

    private fun setupListeners() {
        binding.toolbarPlayer.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.play.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.like.setOnClickListener {
            viewModel.onFavoriteButtonClicked()
        }

        binding.addPlaylist.isEnabled = true
        binding.addPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.bottomSheetOverlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.bottomSheetNewPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val action = PlayerFragmentDirections
                .actionPlayerFragmentToCreatePlaylistFragment(null)
            findNavController().navigate(action)
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

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.addToPlaylistBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.playlistsBottomSheetRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistsAdapter
        }

        binding.playerRoot.post {
            val targetHeight = (binding.playerRoot.height * 0.6f).toInt()
            bottomSheetBehavior.peekHeight = targetHeight
        }

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
        syncBottomSheetOverlay()
    }

    private fun syncBottomSheetOverlay() {
        val isVisible = bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN
        binding.bottomSheetOverlay.isVisible = isVisible
        binding.bottomSheetOverlay.alpha = if (isVisible) 1f else 0f
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun registerInternetReceiver() {
        if (isInternetReceiverRegistered) return
        ContextCompat.registerReceiver(
            requireContext(),
            internetReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        isInternetReceiverRegistered = true
    }

    private fun unregisterInternetReceiver() {
        if (!isInternetReceiverRegistered) return
        try {
            requireContext().unregisterReceiver(internetReceiver)
        } catch (_: IllegalArgumentException) {
        } finally {
            isInternetReceiverRegistered = false
        }
    }

    private fun showNoInternetToast(context: Context) {
        Toast.makeText(
            context,
            context.getString(R.string.no_internet_connection),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun bindPlayerService() {
        if (isServiceBound) return
        val intent = Intent(requireContext(), PlayerPlaybackService::class.java).apply {
            putExtra(PlayerPlaybackService.EXTRA_TRACK, track)
        }
        requireContext().bindService(intent, playerServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindPlayerService() {
        if (!isServiceBound) return
        try {
            requireContext().unbindService(playerServiceConnection)
        } catch (_: IllegalArgumentException) {
        } finally {
            isServiceBound = false
            viewModel.onServiceDisconnected()
        }
    }

    private fun canShowNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (canShowNotifications()) return
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
