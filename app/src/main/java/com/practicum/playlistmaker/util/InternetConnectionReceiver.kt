package com.practicum.playlistmaker.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class InternetConnectionReceiver(
    private val onConnectionLost: (Context) -> Unit
) : BroadcastReceiver() {

    private var lastConnected: Boolean? = null

    fun resetInitialState(context: Context) {
        lastConnected = NetworkUtils.isNetworkAvailable(context)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val connected = NetworkUtils.isNetworkAvailable(context)
        val previous = lastConnected

        if (previous == true && !connected) {
            onConnectionLost(context)
        }

        lastConnected = connected
    }
}