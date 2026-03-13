package com.practicum.playlistmaker.presentation.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.models.PlaylistUiDto

class PlaylistBottomSheetAdapter(
    private val onItemClick: (PlaylistUiDto) -> Unit
) : ListAdapter<PlaylistUiDto, PlaylistBottomSheetViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBottomSheetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottom_sheet, parent, false)
        return PlaylistBottomSheetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistBottomSheetViewHolder, position: Int) {
        val playlist = getItem(position)
        holder.bind(playlist)
        holder.itemView.setOnClickListener { onItemClick.invoke(playlist) }
    }

    private class DiffCallback : DiffUtil.ItemCallback<PlaylistUiDto>() {
        override fun areItemsTheSame(oldItem: PlaylistUiDto, newItem: PlaylistUiDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlaylistUiDto, newItem: PlaylistUiDto): Boolean {
            return oldItem == newItem
        }
    }
}
