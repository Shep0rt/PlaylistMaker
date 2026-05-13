package com.practicum.playlistmaker.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerTheme

@Composable
fun TrackRow(
    track: TrackUiDto,
    onClick: (TrackUiDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val trackNameSizeDp = dimensionResource(id = R.dimen.search_text_size_trackName)
    val trackArtistSizeDp = dimensionResource(id = R.dimen.search_text_size_artist)
    val trackDurationSizeDp = dimensionResource(id = R.dimen.search_text_size_duration)
    val trackNameSizeSp = with(density) { trackNameSizeDp.toSp() }
    val trackArtistSizeSp = with(density) { trackArtistSizeDp.toSp() }
    val trackDurationSizeSp = with(density) { trackDurationSizeDp.toSp() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(track) }
            .padding(start = 13.dp, end = 12.dp)
            .height(45.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
            error = painterResource(id = R.drawable.ic_music_placeholder),
            placeholder = painterResource(id = R.drawable.ic_music_placeholder),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.trackName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.surface,
                    fontSize = trackNameSizeSp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = track.artistName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = trackArtistSizeSp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_track_dot),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(13.dp),
                    )
                    Text(
                        text = track.trackTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = trackDurationSizeSp,
                        maxLines = 1,
                    )
                }
            }
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Preview(showBackground = true, name = "TrackRow")
@Composable
private fun TrackRowPreview() {
    PlaylistMakerTheme(darkTheme = false) {
        TrackRow(
            track = TrackUiDto(
                id = 1,
                trackName = "Smells Like Teen Spirit",
                artistName = "Nirvana",
                trackTime = "5:01",
                artworkUrl100 = null,
                collectionName = null,
                releaseDate = null,
                primaryGenreName = null,
                country = null,
                previewUrl = null,
            ),
            onClick = {},
        )
    }
}
