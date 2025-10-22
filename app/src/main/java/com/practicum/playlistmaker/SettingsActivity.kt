package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.net.toUri
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var toolbar : MaterialToolbar
    private lateinit var shareTextView: View
    private lateinit var supportTextView: View
    private lateinit var userAgreementTextView: View
    private lateinit var themeSwitcher: SwitchMaterial
    private val app by lazy { applicationContext as App }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val rootView = findViewById<View>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBarInsets.top)
            insets
        }

        toolbar = findViewById(R.id.settings_toolbar)
        shareTextView = findViewById(R.id.share_text)
        supportTextView = findViewById(R.id.support)
        userAgreementTextView = findViewById(R.id.user_agreement)
        themeSwitcher = findViewById(R.id.theme_switch)

        //Синхронизируем UI свитчер с значением ищ SharedPreferences
        themeSwitcher.isChecked = app.themeRepository.isDarkThemeEnabled()

        //Действия при клике на кнопку "Назад" внутри раздела "Настройки"
        toolbar.setNavigationOnClickListener {
            finish()
        }

        //Действия при клике на свитчер темы
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            app.switchTheme(checked)
        }

        //Действия при клике на кнопку "Поделится приложением" внутри раздела "Настройки"
        shareTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share_app_title)))
        }

        //Действия при клике на кнопку "Написать в поддержку" внутри раздела "Настройки"
        supportTextView.setOnClickListener {
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

        //Действия при клике на кнопку "Пользовательское соглашение" внутри раздела "Настройки"
        userAgreementTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, getString(R.string.practicum_offer).toUri())
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.error_not_app_link), Toast.LENGTH_SHORT).show()
            }
        }
    }
}