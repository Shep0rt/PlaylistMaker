package com.practicum.playlistmaker.presentation.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.models.TrackUiDto

class TrackAdapter(private var tracks: List<TrackUiDto>,
                   private val onItemClick: ((TrackUiDto) -> Unit)?
) : RecyclerView.Adapter<TracksViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TracksViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)

        if (onItemClick != null) {
            holder.itemView.isClickable = true
            holder.itemView.setOnClickListener { onItemClick.invoke(track) }
        } else {
            holder.itemView.isClickable = false
            holder.itemView.setOnClickListener(null)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<TrackUiDto>) {
        tracks = newList
        notifyDataSetChanged()
    }

}