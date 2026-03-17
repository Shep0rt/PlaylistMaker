package com.practicum.playlistmaker.presentation.media.playlist

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.presentation.models.PlaylistDetailsUiDto
import com.practicum.playlistmaker.presentation.models.PlaylistEditUiDto
import com.practicum.playlistmaker.presentation.search.TrackAdapter
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import com.practicum.playlistmaker.util.getRuQuantityString
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<PlaylistFragmentArgs>()
    private val viewModel: PlaylistViewModel by viewModel {
        parametersOf(args.playlistId)
    }

    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<View>
    private val menuBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            val binding = _binding ?: return
            val isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            binding.playlistMenuOverlay.isVisible = isVisible
            binding.playlistMenuOverlay.alpha = if (isVisible) 1f else 0f
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            val binding = _binding ?: return
            if (slideOffset > 0f) {
                binding.playlistMenuOverlay.isVisible = true
                binding.playlistMenuOverlay.alpha = slideOffset
            }
        }
    }

    private var currentPlaylist: PlaylistDetailsUiDto? = null
    private var currentTracks: List<TrackUiDto> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupTracksList()
        setupBottomSheet()
        setupMenuBottomSheet()
        setupActions()
        observeState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::menuBottomSheetBehavior.isInitialized) {
            menuBottomSheetBehavior.removeBottomSheetCallback(menuBottomSheetCallback)
        }
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (::menuBottomSheetBehavior.isInitialized) {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.playlistMenuOverlay.isVisible = false
            binding.playlistMenuOverlay.alpha = 0f
        }
    }

    private fun setupToolbar() {
        binding.playlistToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupActions() {
        binding.playlistShare.setOnClickListener { sharePlaylist() }
        binding.playlistMenu.setOnClickListener { showMenu() }
        binding.playlistMenuOverlay.setOnClickListener { hideMenu() }
        binding.playlistMenuShare.setOnClickListener {
            hideMenu()
            sharePlaylist()
        }
        binding.playlistMenuEdit.setOnClickListener {
            hideMenu()
            val playlist = currentPlaylist ?: return@setOnClickListener
            val editData = PlaylistEditUiDto(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                coverPath = playlist.coverPath
            )
            val action = PlaylistFragmentDirections
                .actionPlaylistFragmentToCreatePlaylistFragment(editData)
            findNavController().navigate(action)
        }
        binding.playlistMenuDelete.setOnClickListener {
            hideMenu()
            showDeletePlaylistDialog()
        }
    }

    private fun observeState() {
        viewModel.playlistState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistUiState.Content -> renderContent(state.playlist)
                PlaylistUiState.Loading -> Unit
                PlaylistUiState.NotFound -> Unit
            }
        }

        viewModel.playlistTracksState.observe(viewLifecycleOwner) { tracks ->
            val isEmpty = tracks.isEmpty()
            binding.playlistTracksEmpty.isVisible = isEmpty
            binding.playlistTracksRecycler.isVisible = !isEmpty
            currentTracks = tracks
            tracksAdapter.submitList(tracks)
        }

        viewModel.playlistRemovedState.observe(viewLifecycleOwner) { removed ->
            if (removed == true) {
                findNavController().navigateUp()
                viewModel.onPlaylistRemovedHandled()
            }
        }
    }

    private fun renderContent(state: PlaylistDetailsUiDto) {
        currentPlaylist = state
        binding.playlistName.text = state.name

        val description = state.description
        binding.playlistDescription.isVisible = !description.isNullOrBlank()
        binding.playlistDescription.text = description.orEmpty()

        val durationText = requireContext().getRuQuantityString(
            R.plurals.minutes_count,
            state.durationMinutes,
            state.durationMinutes
        )
        val trackCountText = requireContext().getRuQuantityString(
            R.plurals.track_count,
            state.trackCount,
            state.trackCount
        )
        binding.playlistMeta.text = getString(
            R.string.playlist_meta_format,
            durationText,
            trackCountText
        )

        renderCover(state.coverPath)
        renderMenuHeader(state)
    }

    private fun setupTracksList() {
        tracksAdapter = TrackAdapter(
            tracks = emptyList(),
            onItemLongClick = { track ->
                showDeleteTrackDialog(track.id)
            }
        ) { track ->
            val action = PlaylistFragmentDirections.actionPlaylistFragmentToPlayerFragment(track)
            findNavController().navigate(action)
        }
        binding.playlistTracksRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistTracksRecycler.adapter = tracksAdapter
    }

    private fun setupBottomSheet() {
        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistTracksBottomSheet)
        tracksBottomSheetBehavior.isHideable = false
        binding.playlistRoot.post {
            val offset = binding.playlistMenu.bottom +
                resources.getDimensionPixelSize(R.dimen.playlist_bottom_sheet_offset)
            tracksBottomSheetBehavior.peekHeight =
                (binding.playlistRoot.height - offset).coerceAtLeast(0)
            tracksBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setupMenuBottomSheet() {
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistMenuBottomSheet)
        menuBottomSheetBehavior.isHideable = true
        menuBottomSheetBehavior.isFitToContents = false
        menuBottomSheetBehavior.halfExpandedRatio = 0.45f
        menuBottomSheetBehavior.skipCollapsed = true
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        menuBottomSheetBehavior.addBottomSheetCallback(menuBottomSheetCallback)
    }

    private fun showMenu() {
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    private fun hideMenu() {
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showDeleteTrackDialog(trackId: Long) {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Playlist_AlertDialog)
            .setMessage(R.string.playlist_track_delete_message)
            .setNegativeButton(R.string.dialog_no, null)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                viewModel.removeTrack(trackId)
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        val playlist = currentPlaylist ?: return
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Playlist_AlertDialog)
            .setMessage(getString(R.string.playlist_delete_message, playlist.name))
            .setNegativeButton(R.string.dialog_no, null)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                viewModel.removePlaylist()
            }
            .show()
    }

    private fun renderCover(coverPath: String?) {
        val placeholder = R.drawable.ic_not_cover_placeholder312
        val coverSource = coverPath?.let { File(it) } ?: placeholder

        Glide.with(this)
            .load(coverSource)
            .transform(CenterCrop())
            .placeholder(placeholder)
            .error(placeholder)
            .into(binding.playlistCover)
    }

    private fun renderMenuHeader(state: PlaylistDetailsUiDto) {
        binding.playlistMenuName.text = state.name
        binding.playlistMenuCount.text = requireContext().getRuQuantityString(
            R.plurals.track_count,
            state.trackCount,
            state.trackCount
        )
        val radius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            2f,
            resources.displayMetrics
        ).toInt()
        val placeholder = R.drawable.ic_not_cover_placeholder312
        val coverSource = state.coverPath?.let { File(it) } ?: placeholder

        Glide.with(this)
            .load(coverSource)
            .transform(CenterCrop(), RoundedCorners(radius))
            .placeholder(placeholder)
            .error(placeholder)
            .into(binding.playlistMenuCover)
    }

    private fun sharePlaylist() {
        val playlist = currentPlaylist ?: return
        if (currentTracks.isEmpty()) {
            Toast.makeText(requireContext(), R.string.playlist_share_empty, Toast.LENGTH_SHORT).show()
            return
        }

        val shareText = buildShareText(playlist, currentTracks)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.playlist_share_title)))
    }

    private fun buildShareText(
        playlist: PlaylistDetailsUiDto,
        tracks: List<TrackUiDto>
    ): String {
        val builder = StringBuilder()
        builder.appendLine("Название: ${playlist.name}")
        val description = playlist.description
        if (!description.isNullOrBlank()) {
            builder.appendLine("Описание: $description")
        }
        val trackCountText = requireContext().getRuQuantityString(
            R.plurals.track_count,
            tracks.size,
            tracks.size
        )
        builder.appendLine(trackCountText)
        builder.appendLine("Список треков:")

        tracks.forEachIndexed { index, track ->
            builder.appendLine(
                "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})"
            )
        }

        return builder.toString().trimEnd()
    }
}
