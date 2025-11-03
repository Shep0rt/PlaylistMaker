package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.view.WindowCompat
import com.practicum.playlistmaker.presentation.media.MediaLibraryActivity
import com.practicum.playlistmaker.presentation.search.SearchActivity
import com.practicum.playlistmaker.presentation.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rootView = findViewById<View>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(
                top = statusBarInsets.top,
                bottom = navBarInsets.bottom
            )
            insets
        }

        val buttonSearch = findViewById<Button>(R.id.btn_search)
        val buttonMediaLibrary = findViewById<Button>(R.id.btn_media_library)
        val buttonSettings = findViewById<Button>(R.id.btn_settings)

        //Действия при клике на кнопку "Поиск" на главном экране
        buttonSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        //Действия при клике на кнопку "Медиатека" на главном экране
        buttonMediaLibrary.setOnClickListener {
            val intent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(intent)
        }

        //Действия при клике на кнопку "Настройки" на главном экране
        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}