package dev.chara.tasks.network

import kotlinx.coroutines.flow.Flow

expect class ConnectivityStatusManager {
    val isInternetConnected: Flow<Boolean>
}