package com.practicum.playlistmaker.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.models.PlaylistUiDto
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.util.getRuQuantityString
import java.io.File

@Composable
fun PlaylistCard(
    playlist: PlaylistUiDto,
    onClick: (PlaylistUiDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val placeholder = R.drawable.ic_not_cover_placeholder312
    val coverModel = playlist.coverPath?.let { File(it) } ?: placeholder

    Column(
        modifier = modifier
            .clickable { onClick(playlist) }
    ) {
        AsyncImage(
            model = coverModel,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = placeholder),
            error = painterResource(id = placeholder),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = playlist.name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.surface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = context.getRuQuantityString(R.plurals.track_count, playlist.trackCount, playlist.trackCount),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.surface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true, name = "PlaylistCard")
@Composable
private fun PlaylistCardPreview() {
    PlaylistMakerTheme(darkTheme = false) {
        PlaylistCard(
            playlist = PlaylistUiDto(
                id = 1,
                name = "My Playlist",
                description = null,
                coverPath = null,
                trackCount = 12,
            ),
            onClick = {},
        )
    }
}