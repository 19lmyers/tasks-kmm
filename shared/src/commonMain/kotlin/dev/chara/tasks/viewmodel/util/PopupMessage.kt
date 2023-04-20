package dev.chara.tasks.viewmodel.util

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import dev.chara.tasks.data.ApiError
import dev.chara.tasks.data.ClientError
import dev.chara.tasks.data.DataError
import kotlinx.coroutines.flow.MutableSharedFlow

data class PopupMessage(val text: String, val action: Action?) {
    data class Action(val text: String, val function: () -> Unit)
}

suspend fun <V> MutableSharedFlow<PopupMessage>.emitAsMessage(
    result: Result<V, DataError>,
    defaultErrorMessage: String = "An unknown error occurred"
) {
    emit(
        result,
        defaultErrorMessage = defaultErrorMessage,
        successMessage = null,
        action = null
    )
}

suspend fun <V> MutableSharedFlow<PopupMessage>.emitAsMessage(
    result: Result<V, DataError>,
    defaultErrorMessage: String = "An unknown error occurred",
    successMessage: String,
) {
    emit(
        result,
        defaultErrorMessage = defaultErrorMessage,
        successMessage = successMessage,
        action = null
    )
}

suspend fun <V> MutableSharedFlow<PopupMessage>.emitAsMessage(
    result: Result<V, DataError>,
    defaultErrorMessage: String = "An unknown error occurred",
    successMessage: String,
    action: PopupMessage.Action? = null,
) {
    emit(
        result,
        defaultErrorMessage = defaultErrorMessage,
        successMessage = successMessage,
        action = action
    )
}

private suspend fun <V> MutableSharedFlow<PopupMessage>.emit(
    result: Result<V, DataError>,
    defaultErrorMessage: String,
    successMessage: String? = null,
    action: PopupMessage.Action? = null,
) {
    result.mapBoth(
        success = {
            successMessage?.let {
                PopupMessage(successMessage, action)
            }
        },
        failure = { error ->
            val message = when (error) {
                is ApiError -> {
                    error.message
                }

                is ClientError -> {
                    error.exception.message
                }

                else -> {
                    null
                }
            }

            PopupMessage(message ?: defaultErrorMessage, action = null)
        }
    )?.let { message ->
        emit(message)
    }
}

suspend fun MutableSharedFlow<PopupMessage>.emitAsMessage(
    message: String,
    action: PopupMessage.Action? = null,
) {
    emit(PopupMessage(message, action))
}