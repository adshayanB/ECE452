package com.example.farmeraid.data.source

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow

// Reference: https://developer.android.com/training/monitoring-device-state/connectivity-status-type

class NetworkMonitor (
    context: Context
) {
    val source: MutableStateFlow<Source> = MutableStateFlow(Source.DEFAULT)

    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            source.value = Source.DEFAULT
        }

        // lost network connection
        override fun onLost(network: Network) {
            source.value = Source.CACHE
        }
    }

    fun getSource(): Source {
        return source.value
    }

    init {
        val connectivityManager = getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }
}