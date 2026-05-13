package com.practicum.playlistmaker.presentation.media.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.media.favorite.FavoriteTracksState
import com.practicum.playlistmaker.presentation.media.playlist.PlaylistsState
import com.practicum.playlistmaker.presentation.models.PlaylistUiDto
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import com.practicum.playlistmaker.presentation.ui.components.PlaylistCard
import com.practicum.playlistmaker.presentation.ui.components.TrackRow
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerScreenTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaLibraryScreen(
    favoriteTracksState: FavoriteTracksState,
    playlistsState: PlaylistsState,
    onTrackClick: (TrackUiDto) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onPlaylistClick: (PlaylistUiDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTabIndex by rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.media_library),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            windowInsets = WindowInsets(0, 0, 0, 0),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.surface,
            ),
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.surface,
            indicator = { tabPositions ->
                val currentTabPosition = tabPositions[selectedTabIndex]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.BottomStart)
                        .offset(x = currentTabPosition.left)
                        .width(currentTabPosition.width)
                ) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                    )
                }
            },
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Text(
                        text = stringResource(id = R.string.favorite_tracks),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.surface,
                    )
                },
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Text(
                        text = stringResource(id = R.string.playlists),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.surface,
                    )
                },
            )
        }

        when (selectedTabIndex) {
            0 -> FavoriteTracksTab(
                state = favoriteTracksState,
                onTrackClick = onTrackClick,
                modifier = Modifier.fillMaxSize(),
            )
            else -> PlaylistsTab(
                state = playlistsState,
                onCreatePlaylistClick = onCreatePlaylistClick,
                onPlaylistClick = onPlaylistClick,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private fun previewTrack(id: Long) = TrackUiDto(
    id = id,
    trackName = "Track $id",
    artistName = "Artist",
    trackTime = "3:45",
    artworkUrl100 = null,
    collectionName = null,
    releaseDate = null,
    primaryGenreName = null,
    country = null,
    previewUrl = null,
)

private fun previewPlaylist(id: Long) = PlaylistUiDto(
    id = id,
    name = "Playlist $id",
    description = null,
    coverPath = null,
    trackCount = 12,
)

@Preview(showBackground = true, name = "MediaLibrary (Favorites)")
@Composable
private fun MediaLibraryScreenPreviewFavorites() {
    PlaylistMakerTheme(darkTheme = false, screenTheme = PlaylistMakerScreenTheme.MediaLibrary) {
        MediaLibraryScreen(
            favoriteTracksState = FavoriteTracksState.Content(listOf(previewTrack(1), previewTrack(2))),
            playlistsState = PlaylistsState.Content(listOf(previewPlaylist(1), previewPlaylist(2), previewPlaylist(3))),
            onTrackClick = {},
            onCreatePlaylistClick = {},
            onPlaylistClick = {},
        )
    }
}

@Preview(showBackground = true, name = "MediaLibrary (Empty Playlists)")
@Composable
private fun MediaLibraryScreenPreviewEmptyPlaylists() {
    PlaylistMakerTheme(darkTheme = true, screenTheme = PlaylistMakerScreenTheme.MediaLibrary) {
        MediaLibraryScreen(
            favoriteTracksState = FavoriteTracksState.Empty,
            playlistsState = PlaylistsState.Empty,
            onTrackClick = {},
            onCreatePlaylistClick = {},
            onPlaylistClick = {},
        )
    }
}

@Composable
private fun FavoriteTracksTab(
    state: FavoriteTracksState,
    onTrackClick: (TrackUiDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        FavoriteTracksState.Loading -> Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        FavoriteTracksState.Empty -> CenterPlaceholder(
            imageRes = R.drawable.ic_search_not_result,
            text = stringResource(id = R.string.media_library_empty),
            modifier = modifier,
        )
        is FavoriteTracksState.Content -> LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.tracks, key = { it.id }) { track ->
                TrackRow(track = track, onClick = onTrackClick)
            }
        }
    }
}

@Composable
private fun PlaylistsTab(
    state: PlaylistsState,
    onCreatePlaylistClick: () -> Unit,
    onPlaylistClick: (PlaylistUiDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        PlaylistsState.Loading -> Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        PlaylistsState.Empty -> PlaylistsEmptyPlaceholder(
            onCreatePlaylistClick = onCreatePlaylistClick,
            modifier = modifier,
        )
        is PlaylistsState.Content -> Column(modifier = modifier) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onCreatePlaylistClick,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(text = stringResource(id = R.string.new_playlist))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(state.playlists, key = { it.id }) { playlist ->
                    PlaylistCard(
                        playlist = playlist,
                        onClick = onPlaylistClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistsEmptyPlaceholder(
    onCreatePlaylistClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonMarginTop = dimensionResource(id = R.dimen.media_library_playlists_button_margin_top)
    val imageMarginTop = dimensionResource(id = R.dimen.media_library_no_playlists_image_margin_top)
    val textMarginTop = dimensionResource(id = R.dimen.media_library_placeholder_text_margin_top)

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(buttonMarginTop))
        Button(
            onClick = onCreatePlaylistClick,
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Text(text = stringResource(id = R.string.new_playlist))
        }

        Spacer(modifier = Modifier.height(imageMarginTop))
        androidx.compose.material3.Icon(
            painter = painterResource(id = R.drawable.ic_search_not_result),
            contentDescription = null,
            tint = androidx.compose.ui.graphics.Color.Unspecified,
            modifier = Modifier.size(120.dp),
        )

        Spacer(modifier = Modifier.height(textMarginTop))
        Text(
            text = stringResource(id = R.string.media_library_no_playlist),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.surface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun CenterPlaceholder(
    imageRes: Int,
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 106.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        androidx.compose.material3.Icon(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            tint = androidx.compose.ui.graphics.Color.Unspecified,
            modifier = Modifier.size(120.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.surface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}