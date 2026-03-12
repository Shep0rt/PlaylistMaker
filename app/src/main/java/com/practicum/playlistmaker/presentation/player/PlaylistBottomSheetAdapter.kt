package com.practicum.playlistmaker.presentation.player

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.models.PlaylistUiDto

class PlaylistBottomSheetAdapter(
    private var playlists: List<PlaylistUiDto>,
    private val onItemClick: (PlaylistUiDto) -> Unit
) : RecyclerView.Adapter<PlaylistBottomSheetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBottomSheetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottom_sheet, parent, false)
        return PlaylistBottomSheetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistBottomSheetViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener { onItemClick.invoke(playlist) }
    }

    override fun getItemCount(): Int = playlists.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<PlaylistUiDto>) {
        playlists = newList
        notifyDataSetChanged()
    }
}