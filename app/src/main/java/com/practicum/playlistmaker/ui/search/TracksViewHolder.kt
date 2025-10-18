package com.practicum.playlistmaker.ui.search

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.model.Track

class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artist)
    private val trackTime: TextView = itemView.findViewById(R.id.duration)
    private val artworkUrl100: ImageView = itemView.findViewById(R.id.albumCover)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .transform(
                FitCenter(),
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        2f,
                        itemView.resources.displayMetrics
                    ).toInt()
                )
            )
            .placeholder(R.drawable.ic_music_placeholder)
            .error(R.drawable.ic_music_placeholder)
            .into(artworkUrl100)
    }
}