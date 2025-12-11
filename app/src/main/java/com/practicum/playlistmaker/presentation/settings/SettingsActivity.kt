package com.practicum.playlistmaker.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        setupToolbar()
        initThemeSwitcher()
        setupThemeSwitchListener()
        setupShareListener()
        setupSupportListener()
        setupAgreementListener()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBarInsets.top)
            insets
        }
    }

    private fun setupToolbar() {
        //Действия при клике на кнопку "Назад" внутри раздела "Настройки"
        binding.settingsToolbar.setNavigationOnClickListener { finish() }
    }

    private fun initThemeSwitcher() {
        // Инициируем UI текущим значением темы
        binding.themeSwitch.isChecked = viewModel.isDarkMode()
    }

    private fun setupThemeSwitchListener() {
        //Действия при клике на свитчер темы
        binding.themeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.setDarkMode(checked)

            AppCompatDelegate.setDefaultNightMode(
                if (checked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun setupShareListener() {
        //Действия при клике на кнопку "Поделится приложением" внутри раздела "Настройки"
        binding.shareText.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share_app_title)))
        }
    }

    private fun setupSupportListener() {
        //Действия при клике на кнопку "Написать в поддержку" внутри раздела "Настройки"
        binding.support.setOnClickListener {
            val email = "mailto:" + Uri.encode(getString(R.string.email_address)) +
                    "?subject=" + Uri.encode(getString(R.string.support_subject)) +
                    "&body=" + Uri.encode(getString(R.string.support_body))

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = email.toUri()
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.error_not_app_email), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAgreementListener() {
        //Действия при клике на кнопку "Пользовательское соглашение" внутри раздела "Настройки"
        binding.userAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, getString(R.string.practicum_offer).toUri())
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.error_not_app_link), Toast.LENGTH_SHORT).show()
            }
        }
    }
}