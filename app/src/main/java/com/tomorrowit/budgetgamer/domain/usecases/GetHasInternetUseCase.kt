package com.tomorrowit.budgetgamer.domain.usecases

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class GetHasInternetUseCase(
    private val connectivityManager: ConnectivityManager,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    private val activeNetwork = connectivityManager.activeNetwork
    private val initialConnected = activeNetwork != null &&
            connectivityManager.getNetworkCapabilities(activeNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

    private val _connectivityStatus = MutableStateFlow(initialConnected)

    init {
        monitorConnectivity()
    }

    operator fun invoke(): StateFlow<Boolean> {
        return _connectivityStatus.asStateFlow()
    }

    private fun monitorConnectivity() {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                _connectivityStatus.value = true
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                _connectivityStatus.value = false
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val hasInternet =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                _connectivityStatus.value = hasInternet
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        _connectivityStatus.subscriptionCount
            .map { count -> count > 0 }
            .distinctUntilChanged()
            .onEach { hasSubscribers ->
                if (hasSubscribers) {
                    connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
                } else {
                    connectivityManager.unregisterNetworkCallback(networkCallback)
                }
            }
            .launchIn(CoroutineScope(SupervisorJob() + coroutineDispatcher))
    }
}