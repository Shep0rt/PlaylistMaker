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
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
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

        val toolbar = findViewById<MaterialToolbar>(R.id.settings_toolbar)
        val shareTextView = findViewById<MaterialTextView>(R.id.share_text)
        val supportTextView = findViewById<MaterialTextView>(R.id.support)
        val userAgreementTextView = findViewById<MaterialTextView>(R.id.user_agreement)

        //Действия при клике на кнопку "Назад" внутри раздела "Настройки"
        toolbar.setNavigationOnClickListener {
            finish()
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