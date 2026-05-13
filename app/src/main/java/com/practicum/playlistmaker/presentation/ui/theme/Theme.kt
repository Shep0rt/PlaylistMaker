package com.practicum.playlistmaker.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.annotation.ColorRes
import com.practicum.playlistmaker.R

enum class PlaylistMakerScreenTheme {
    Settings,
    Search,
    MediaLibrary,
}

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    screenTheme: PlaylistMakerScreenTheme = PlaylistMakerScreenTheme.Search,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) darkSchemeFor(screenTheme) else lightSchemeFor(screenTheme)

    MaterialTheme(
        colorScheme = colors,
        typography = PlaylistMakerTypography,
        content = content,
    )
}

@Composable
private fun lightSchemeFor(screenTheme: PlaylistMakerScreenTheme): ColorScheme {
    return when (screenTheme) {
        PlaylistMakerScreenTheme.Settings -> lightColorScheme(
            primary = c(R.color.white),
            onPrimary = c(R.color.black),
            background = c(R.color.white),
            onBackground = c(R.color.black),
            surface = c(R.color.black),
            onSurface = c(R.color.black),
            outline = c(R.color.gray),
        )
        PlaylistMakerScreenTheme.Search -> lightColorScheme(
            primary = c(R.color.white),
            onPrimary = c(R.color.black),
            background = c(R.color.white),
            onBackground = c(R.color.black),
            surface = c(R.color.black),
            onSurface = c(R.color.black),
            surfaceVariant = c(R.color.light_gray),
            onSurfaceVariant = c(R.color.gray),
            outline = c(R.color.gray),
        )
        PlaylistMakerScreenTheme.MediaLibrary -> lightColorScheme(
            primary = c(R.color.white),
            onPrimary = c(R.color.black),
            background = c(R.color.white),
            onBackground = c(R.color.black),
            surface = c(R.color.black),
            onSurface = c(R.color.black),
            outline = c(R.color.gray),
        )
    }
}

@Composable
private fun darkSchemeFor(screenTheme: PlaylistMakerScreenTheme): ColorScheme {
    return when (screenTheme) {
        PlaylistMakerScreenTheme.Settings -> darkColorScheme(
            primary = c(R.color.black),
            onPrimary = c(R.color.white),
            background = c(R.color.black),
            onBackground = c(R.color.white),
            surface = c(R.color.white),
            onSurface = c(R.color.white),
            outline = c(R.color.white),
        )
        PlaylistMakerScreenTheme.Search -> darkColorScheme(
            primary = c(R.color.black),
            onPrimary = c(R.color.white),
            background = c(R.color.black),
            onBackground = c(R.color.white),
            surface = c(R.color.white),
            onSurface = c(R.color.white),
            surfaceVariant = c(R.color.white),
            onSurfaceVariant = c(R.color.black),
            outline = c(R.color.white),
        )
        PlaylistMakerScreenTheme.MediaLibrary -> darkColorScheme(
            primary = c(R.color.black),
            onPrimary = c(R.color.white),
            background = c(R.color.black),
            onBackground = c(R.color.white),
            surface = c(R.color.white),
            onSurface = c(R.color.white),
            outline = c(R.color.white),
        )
    }
}

@Composable
private fun c(@ColorRes id: Int): Color = colorResource(id = id)