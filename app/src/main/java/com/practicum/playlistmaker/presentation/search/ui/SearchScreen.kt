package com.practicum.playlistmaker.presentation.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.models.TrackUiDto
import com.practicum.playlistmaker.presentation.search.SearchState
import com.practicum.playlistmaker.presentation.ui.components.TrackRow
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerScreenTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    onQueryChanged: (String) -> Unit,
    onSearchAction: (String) -> Unit,
    onClearHistory: () -> Unit,
    onRetry: (String) -> Unit,
    onTrackClickFromSearch: (TrackUiDto) -> Unit,
    onTrackClickFromHistory: (TrackUiDto) -> Unit,
    onQueryFieldFocusedAndEmpty: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.search),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            windowInsets = WindowInsets(0, 0, 0, 0),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.surface,
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        SearchField(
            query = query,
            onQueryChange = {
                query = it
                onQueryChanged(it)
            },
            onClearClick = {
                query = ""
                onQueryChanged("")
                keyboardController?.hide()
            },
            onFocusChanged = { focused ->
                if (focused && query.isEmpty()) onQueryFieldFocusedAndEmpty()
            },
            onImeSearch = {
                val trimmed = query.trim()
                onSearchAction(trimmed)
                keyboardController?.hide()
            },
        )

        Spacer(modifier = Modifier.height(42.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (state) {
                SearchState.Idle -> Unit
                SearchState.Loading -> LoadingPlaceholder(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 98.dp),
                )
                is SearchState.Content -> TracksList(
                    tracks = state.tracks,
                    onTrackClick = onTrackClickFromSearch,
                    modifier = Modifier.fillMaxSize(),
                )
                SearchState.Empty -> CenterMessagePlaceholder(
                    imageRes = R.drawable.ic_search_not_result,
                    text = stringResource(id = R.string.search_track_not_result),
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 110.dp),
                )
                SearchState.Error -> NetworkErrorPlaceholder(
                    query = query.trim(),
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 110.dp),
                )
                is SearchState.History -> {
                    if (state.tracks.isNotEmpty()) {
                        HistoryList(
                            tracks = state.tracks,
                            onTrackClick = onTrackClickFromHistory,
                            onClearHistory = onClearHistory,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

private fun previewTrack(id: Long) = TrackUiDto(
    id = id,
    trackName = "Smells Like Teen Spirit",
    artistName = "Nirvana",
    trackTime = "5:01",
    artworkUrl100 = null,
    collectionName = null,
    releaseDate = null,
    primaryGenreName = null,
    country = null,
    previewUrl = null,
)

@Preview(showBackground = true, name = "Search (Idle)")
@Composable
private fun SearchScreenPreviewIdle() {
    PlaylistMakerTheme(darkTheme = false, screenTheme = PlaylistMakerScreenTheme.Search) {
        SearchScreen(
            state = SearchState.Idle,
            onQueryChanged = {},
            onSearchAction = {},
            onClearHistory = {},
            onRetry = {},
            onTrackClickFromSearch = {},
            onTrackClickFromHistory = {},
            onQueryFieldFocusedAndEmpty = {},
        )
    }
}

@Preview(showBackground = true, name = "Search (Content)")
@Composable
private fun SearchScreenPreviewContent() {
    PlaylistMakerTheme(darkTheme = false, screenTheme = PlaylistMakerScreenTheme.Search) {
        SearchScreen(
            state = SearchState.Content(listOf(previewTrack(1), previewTrack(2), previewTrack(3))),
            onQueryChanged = {},
            onSearchAction = {},
            onClearHistory = {},
            onRetry = {},
            onTrackClickFromSearch = {},
            onTrackClickFromHistory = {},
            onQueryFieldFocusedAndEmpty = {},
        )
    }
}

@Preview(showBackground = true, name = "Search (History)")
@Composable
private fun SearchScreenPreviewHistory() {
    PlaylistMakerTheme(darkTheme = false, screenTheme = PlaylistMakerScreenTheme.Search) {
        SearchScreen(
            state = SearchState.History(listOf(previewTrack(1), previewTrack(2))),
            onQueryChanged = {},
            onSearchAction = {},
            onClearHistory = {},
            onRetry = {},
            onTrackClickFromSearch = {},
            onTrackClickFromHistory = {},
            onQueryFieldFocusedAndEmpty = {},
        )
    }
}

@Preview(showBackground = true, name = "Search (Error)")
@Composable
private fun SearchScreenPreviewError() {
    PlaylistMakerTheme(darkTheme = true, screenTheme = PlaylistMakerScreenTheme.Search) {
        SearchScreen(
            state = SearchState.Error,
            onQueryChanged = {},
            onSearchAction = {},
            onClearHistory = {},
            onRetry = {},
            onTrackClickFromSearch = {},
            onTrackClickFromHistory = {},
            onQueryFieldFocusedAndEmpty = {},
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onImeSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val showClear = query.isNotEmpty()
    val interactionSource = remember { MutableInteractionSource() }
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        focusedTextColor = MaterialTheme.colorScheme.surface,
        unfocusedTextColor = MaterialTheme.colorScheme.surface,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
    )

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(36.dp)
            .onFocusChanged { onFocusChanged(it.isFocused) },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = colorResource(id = R.color.black)),
        cursorBrush = SolidColor(colorResource(id = R.color.blue)),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onImeSearch() }),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = query,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search16),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                trailingIcon = if (showClear) {
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clickable(onClick = onClearClick)
                                .padding(4.dp),
                        )
                    }
                } else null,
                shape = RoundedCornerShape(8.dp),
                colors = colors,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            )
        },
    )
}

@Composable
private fun LoadingPlaceholder(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.size(44.dp),
        color = colorResource(id = R.color.blue),
    )
}

@Composable
private fun TracksList(
    tracks: List<TrackUiDto>,
    onTrackClick: (TrackUiDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(tracks, key = { it.id }) { track ->
            TrackRow(track = track, onClick = onTrackClick)
        }
    }
}

@Composable
private fun HistoryList(
    tracks: List<TrackUiDto>,
    onTrackClick: (TrackUiDto) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clearButtonMarginStart = dimensionResource(id = R.dimen.margin_start_clear_history_button)
    val clearButtonMarginEnd = dimensionResource(id = R.dimen.margin_end_clear_history_button)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = stringResource(id = R.string.you_searched),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.surface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
        }

        items(tracks, key = { it.id }) { track ->
            TrackRow(track = track, onClick = onTrackClick)
        }

        item {
            Button(
                onClick = onClearHistory,
                modifier = Modifier
                    .padding(start = clearButtonMarginStart, end = clearButtonMarginEnd),
                shape = RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(text = stringResource(id = R.string.clear_history))
            }
        }
    }
}

@Composable
private fun CenterMessagePlaceholder(
    imageRes: Int,
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
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

@Composable
private fun NetworkErrorPlaceholder(
    query: String,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search_network_error),
            contentDescription = null,
            tint = androidx.compose.ui.graphics.Color.Unspecified,
            modifier = Modifier.size(120.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.search_track_network_error),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.surface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onRetry(query) },
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Text(text = stringResource(id = R.string.search_retry_button_text))
        }
    }
}