package com.practicum.playlistmaker.presentation.media.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Rect
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel by viewModel<PlaylistsViewModel>()
    private val adapter = PlaylistsAdapter(emptyList())

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupListeners()
        observeState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private fun setupRecycler() {
        binding.playlistsRecyclerView.apply {
            val spacing = resources.getDimensionPixelSize(R.dimen.playlist_grid_spacing)
            addItemDecoration(GridSpacingItemDecoration(2, spacing))
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@PlaylistsFragment.adapter
        }
    }

    private fun setupListeners() {
        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
        }
        binding.emptyPlaceholder.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
        }
    }

    private fun observeState() {
        viewModel.playlistsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsState.Content -> {
                    binding.playlistsContent.isVisible = true
                    binding.playlistsRecyclerView.isVisible = true
                    binding.emptyPlaceholder.root.isVisible = false
                    adapter.submitList(state.playlists)
                }
                PlaylistsState.Empty -> {
                    binding.playlistsContent.isVisible = false
                    binding.emptyPlaceholder.root.isVisible = true
                    adapter.submitList(emptyList())
                }
                PlaylistsState.Loading -> {
                    binding.playlistsContent.isVisible = false
                    binding.emptyPlaceholder.root.isVisible = false
                }
            }
        }
    }

    private class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacingPx: Int
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position == RecyclerView.NO_POSITION) return

            val column = position % spanCount
            val half = spacingPx / 2
            outRect.left = if (column == 0) 0 else half
            outRect.right = if (column == spanCount - 1) 0 else half
        }
    }
}