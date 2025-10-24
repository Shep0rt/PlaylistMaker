package com.practicum.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.model.Track

class TrackAdapter(private var tracks: List<Track>,
                   private val onItemClick: ((Track) -> Unit)?
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
    fun submitList(newList: List<Track>) {
        tracks = newList
        notifyDataSetChanged()
    }

}