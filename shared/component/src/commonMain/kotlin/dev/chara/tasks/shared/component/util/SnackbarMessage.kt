package dev.chara.tasks.shared.component.util

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import dev.chara.tasks.shared.data.ApiError
import dev.chara.tasks.shared.data.ClientError
import dev.chara.tasks.shared.data.DataError
import kotlinx.coroutines.flow.MutableSharedFlow

data class SnackbarMessage(val text: String, val action: Action?) {
    data class Action(val text: String, val function: () -> Unit)
}

suspend fun <V> MutableSharedFlow<SnackbarMessage>.emitAsMessage(
    result: Result<V, DataError>,
    defaultErrorMessage: String = "An unknown error occurred"
) {
    emit(result, defaultErrorMessage = defaultErrorMessage, successMessage = null, action = null)
}

suspend fun <V> MutableSharedFlow<SnackbarMessage>.emitAsMessage(
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

suspend fun <V> MutableSharedFlow<SnackbarMessage>.emitAsMessage(
    result: Result<V, DataError>,
    defaultErrorMessage: String = "An unknown error occurred",
    successMessage: String,
    action: SnackbarMessage.Action? = null,
) {
    emit(
        result,
        defaultErrorMessage = defaultErrorMessage,
        successMessage = successMessage,
        action = action
    )
}

private suspend fun <V> MutableSharedFlow<SnackbarMessage>.emit(
    result: Result<V, DataError>,
    defaultErrorMessage: String,
    successMessage: String? = null,
    action: SnackbarMessage.Action? = null,
) {
    result
        .mapBoth(
            success = { successMessage?.let { SnackbarMessage(successMessage, action) } },
            failure = { error ->
                val message =
                    when (error) {
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

                SnackbarMessage(message ?: defaultErrorMessage, action = null)
            }
        )
        ?.let { message -> emit(message) }
}

suspend fun MutableSharedFlow<SnackbarMessage>.emitAsMessage(
    message: String,
    action: SnackbarMessage.Action? = null,
) {
    emit(SnackbarMessage(message, action))
}
