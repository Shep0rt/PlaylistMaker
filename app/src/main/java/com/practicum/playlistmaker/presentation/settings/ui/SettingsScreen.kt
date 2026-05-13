package com.practicum.playlistmaker.presentation.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerScreenTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsScreen(
    darkThemeEnabled: Boolean,
    onDarkThemeEnabledChange: (Boolean) -> Unit,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onUserAgreementClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.settings),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            windowInsets = WindowInsets(0, 0, 0, 0),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.surface,
            ),
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.settings_toolbar_margin_bottom)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            val paddingStart = dimensionResource(id = R.dimen.material_padding_start)
            val paddingEnd = dimensionResource(id = R.dimen.material_padding_end)
            val rowHeight = dimensionResource(id = R.dimen.settings_row_height)

            SettingsSwitchRow(
                text = stringResource(id = R.string.theme_dark),
                checked = darkThemeEnabled,
                onCheckedChange = onDarkThemeEnabledChange,
                paddingStart = paddingStart,
                paddingEnd = paddingEnd,
                rowHeight = rowHeight,
            )

            SettingsActionRow(
                text = stringResource(id = R.string.share),
                trailingIcon = painterResource(id = R.drawable.ic_share),
                onClick = onShareClick,
                paddingStart = paddingStart,
                paddingEnd = paddingEnd,
                rowHeight = rowHeight,
            )

            SettingsActionRow(
                text = stringResource(id = R.string.support),
                trailingIcon = painterResource(id = R.drawable.ic_support),
                onClick = onSupportClick,
                paddingStart = paddingStart,
                paddingEnd = paddingEnd,
                rowHeight = rowHeight,
            )

            SettingsActionRow(
                text = stringResource(id = R.string.user_agreement),
                trailingIcon = painterResource(id = R.drawable.ic_arrow_right),
                onClick = onUserAgreementClick,
                paddingStart = paddingStart,
                paddingEnd = paddingEnd,
                rowHeight = rowHeight,
            )
        }
    }
}

@Preview(showBackground = true, name = "Settings (Light)")
@Composable
private fun SettingsScreenPreviewLight() {
    PlaylistMakerTheme(darkTheme = false, screenTheme = PlaylistMakerScreenTheme.Settings) {
        SettingsScreen(
            darkThemeEnabled = false,
            onDarkThemeEnabledChange = {},
            onShareClick = {},
            onSupportClick = {},
            onUserAgreementClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Settings (Dark)")
@Composable
private fun SettingsScreenPreviewDark() {
    PlaylistMakerTheme(darkTheme = true, screenTheme = PlaylistMakerScreenTheme.Settings) {
        SettingsScreen(
            darkThemeEnabled = true,
            onDarkThemeEnabledChange = {},
            onShareClick = {},
            onSupportClick = {},
            onUserAgreementClick = {},
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    paddingStart: Dp,
    paddingEnd: Dp,
    rowHeight: Dp,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight)
            .padding(
                start = paddingStart,
                end = paddingEnd,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = colorResource(id = R.color.blue_light),
                checkedThumbColor = colorResource(id = R.color.blue),
                uncheckedTrackColor = colorResource(id = R.color.light_gray),
                uncheckedThumbColor = colorResource(id = R.color.gray),
                checkedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                uncheckedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
            ),
        )
    }
}

@Composable
private fun SettingsActionRow(
    text: String,
    trailingIcon: Painter,
    onClick: () -> Unit,
    paddingStart: Dp,
    paddingEnd: Dp,
    rowHeight: Dp,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight)
            .clickable(onClick = onClick)
            .padding(
                start = paddingStart,
                end = paddingEnd,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = trailingIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
        )
    }
}