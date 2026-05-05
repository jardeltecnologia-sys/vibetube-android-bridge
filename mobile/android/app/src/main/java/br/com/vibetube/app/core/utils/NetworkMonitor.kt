package br.com.vibetube.app.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Observa estado de conectividade. Útil para mostrar OfflineBanner quando
 * o cache estiver sendo exibido por falta de rede.
 */
class NetworkMonitor(private val context: Context) {

    private val cm: ConnectivityManager? =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    fun observe(): Flow<Boolean> = callbackFlow {
        val mgr = cm ?: run {
            trySend(true)  // assume online se não conseguimos checar
            close()
            return@callbackFlow
        }
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(true) }
            override fun onLost(network: Network) { trySend(false) }
            override fun onUnavailable() { trySend(false) }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()
        try {
            mgr.registerNetworkCallback(request, callback)
        } catch (_: Exception) {
            trySend(true)
        }

        // Estado inicial
        val active = mgr.activeNetwork
        val caps = active?.let { mgr.getNetworkCapabilities(it) }
        val online = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        trySend(online)

        awaitClose { try { mgr.unregisterNetworkCallback(callback) } catch (_: Exception) {} }
    }.distinctUntilChanged()
}
