package com.practicum.playlistmaker.presentation.media.playlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.models.PlaylistUiDto

class PlaylistsAdapter(
    private var playlists: List<PlaylistUiDto>
) : RecyclerView.Adapter<PlaylistsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_grid, parent, false)
        return PlaylistsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<PlaylistUiDto>) {
        playlists = newList
        notifyDataSetChanged()
    }
}