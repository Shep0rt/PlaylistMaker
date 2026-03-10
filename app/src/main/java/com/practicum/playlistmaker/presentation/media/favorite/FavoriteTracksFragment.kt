package com.practicum.playlistmaker.presentation.media.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.presentation.media.MediaLibraryFragmentDirections
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import com.practicum.playlistmaker.presentation.search.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private val viewModel by viewModel<FavoriteTracksViewModel>()

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapter() {
        searchAdapter = TrackAdapter(emptyList()) { track ->
            val action = MediaLibraryFragmentDirections.actionMediaLibraryFragmentToPlayerFragment(track)
            findNavController().navigate(action)
        }
        binding.favoriteTracksRecyclerView.adapter = searchAdapter
    }

    private fun setupObservers() {
        viewModel.favoriteTracksState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: FavoriteTracksState) {
        when (state) {
            is FavoriteTracksState.Content -> showContent(state.tracks)
            is FavoriteTracksState.Empty -> showEmpty()
            is FavoriteTracksState.Loading -> showLoading()
        }
    }

    private fun showContent(tracks: List<TrackUiDto>) {
        binding.favoriteTracksRecyclerView.isVisible = true
        binding.favoriteTracksPlaceholder.root.isVisible = false
        searchAdapter.submitList(tracks)
    }

    private fun showEmpty() {
        binding.favoriteTracksRecyclerView.isVisible = false
        binding.favoriteTracksPlaceholder.root.isVisible = true
    }

    private fun showLoading() {
        binding.favoriteTracksRecyclerView.isVisible = false
        binding.favoriteTracksPlaceholder.root.isVisible = false
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}