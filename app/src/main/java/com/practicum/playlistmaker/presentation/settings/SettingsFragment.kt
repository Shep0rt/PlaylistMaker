package com.practicum.playlistmaker.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.settings.ui.SettingsScreen
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.presentation.ui.theme.PlaylistMakerScreenTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val darkThemeEnabled by viewModel.darkModeEnabled.observeAsState(
                    initial = viewModel.darkModeEnabled.value ?: false
                )

                LaunchedEffect(darkThemeEnabled) {
                    AppCompatDelegate.setDefaultNightMode(
                        if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
                        else AppCompatDelegate.MODE_NIGHT_NO
                    )
                }

                PlaylistMakerTheme(
                    darkTheme = darkThemeEnabled,
                    screenTheme = PlaylistMakerScreenTheme.Settings,
                ) {
                    SettingsScreen(
                        darkThemeEnabled = darkThemeEnabled,
                        onDarkThemeEnabledChange = viewModel::setDarkMode,
                        onShareClick = ::shareApp,
                        onSupportClick = ::openSupport,
                        onUserAgreementClick = ::openUserAgreement,
                    )
                }
            }
        }
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_app_title)))
    }

    private fun openSupport() {
        val email = "mailto:" + Uri.encode(getString(R.string.email_address)) +
            "?subject=" + Uri.encode(getString(R.string.support_subject)) +
            "&body=" + Uri.encode(getString(R.string.support_body))

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = email.toUri()
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), getString(R.string.error_not_app_email), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUserAgreement() {
        val intent = Intent(Intent.ACTION_VIEW, getString(R.string.practicum_offer).toUri())
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), getString(R.string.error_not_app_link), Toast.LENGTH_SHORT).show()
        }
    }
}