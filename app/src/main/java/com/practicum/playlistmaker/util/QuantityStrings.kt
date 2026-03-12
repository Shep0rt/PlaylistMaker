package com.practicum.playlistmaker.util

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.PluralsRes
import java.util.Locale

fun Context.getRuQuantityString(
    @PluralsRes id: Int,
    quantity: Int,
    vararg formatArgs: Any
): String {
    val config = Configuration(resources.configuration)
    config.setLocale(Locale.forLanguageTag("ru"))
    val localizedResources = createConfigurationContext(config).resources
    return localizedResources.getQuantityString(id, quantity, *formatArgs)
}
