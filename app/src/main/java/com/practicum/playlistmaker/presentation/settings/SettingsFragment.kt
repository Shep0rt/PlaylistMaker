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
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupThemeObserver()
        setupThemeSwitchListener()
        setupShareListener()
        setupSupportListener()
        setupAgreementListener()
    }

    private fun setupThemeObserver() {
        viewModel.darkModeEnabled.observe(viewLifecycleOwner) { isDark ->
            if (binding.themeSwitch.isChecked != isDark) {
                binding.themeSwitch.isChecked = isDark
            }

            AppCompatDelegate.setDefaultNightMode(
                if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun setupThemeSwitchListener() {
        binding.themeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.setDarkMode(checked)
        }
    }

    private fun setupShareListener() {
        binding.shareText.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share_app_title)))
        }
    }

    private fun setupSupportListener() {
        binding.support.setOnClickListener {
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
    }

    private fun setupAgreementListener() {
        binding.userAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, getString(R.string.practicum_offer).toUri())
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_not_app_link), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
