package com.practicum.playlistmaker.presentation.media.playlist

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.models.PlaylistUiDto
import com.practicum.playlistmaker.util.getRuQuantityString
import java.io.File

class PlaylistsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val cover: ImageView = itemView.findViewById(R.id.playlist_cover)
    private val name: TextView = itemView.findViewById(R.id.playlist_name)
    private val trackCount: TextView = itemView.findViewById(R.id.playlist_track_count)

    fun bind(playlist: PlaylistUiDto) {
        name.text = playlist.name
        trackCount.text = itemView.context.getRuQuantityString(
            R.plurals.track_count,
            playlist.trackCount,
            playlist.trackCount
        )

        val radius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8f,
            itemView.resources.displayMetrics
        ).toInt()

        val placeholder = R.drawable.ic_not_cover_placeholder312
        val coverSource = playlist.coverPath?.let { File(it) } ?: placeholder

        Glide.with(itemView)
            .load(coverSource)
            .transform(CenterCrop(), RoundedCorners(radius))
            .placeholder(placeholder)
            .error(placeholder)
            .into(cover)
    }
}