package dev.chara.tasks.network

import kotlinx.coroutines.flow.Flow

actual class ConnectivityStatusManager {
    actual val isInternetConnected: Flow<Boolean> = TODO("Not yet implemented")
}