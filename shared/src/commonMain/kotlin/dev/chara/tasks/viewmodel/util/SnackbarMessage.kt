package dev.chara.tasks.viewmodel.util

import dev.chara.tasks.data.DataResult
import kotlinx.coroutines.flow.MutableSharedFlow

data class SnackbarMessage(val text: String, val action: Action?) {
    data class Action(val text: String, val function: () -> Unit)
}

suspend fun <T> MutableSharedFlow<SnackbarMessage>.emitAsMessage(
    result: DataResult<T>,
    defaultErrorMessage: String = "An unknown error occurred"
) {
    emit(
        result,
        defaultErrorMessage = defaultErrorMessage,
        successMessage = null,
        action = null
    )
}

suspend fun <T> MutableSharedFlow<SnackbarMessage>.emitAsMessage(
    result: DataResult<T>,
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

suspend fun <T> MutableSharedFlow<SnackbarMessage>.emitAsMessage(
    result: DataResult<T>,
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

private suspend fun <T> MutableSharedFlow<SnackbarMessage>.emit(
    result: DataResult<T>,
    defaultErrorMessage: String,
    successMessage: String? = null,
    action: SnackbarMessage.Action? = null,
) {
    result.fold(
        onSuccess = {
            successMessage?.let {
                SnackbarMessage(successMessage, action)
            }
        },
        onFailure = { errorMessage ->
            SnackbarMessage(errorMessage ?: defaultErrorMessage, action = null)
        }
    )?.let { message ->
        emit(message)
    }
}

suspend fun MutableSharedFlow<SnackbarMessage>.emitAsMessage(
    message: String,
    action: SnackbarMessage.Action? = null,
) {
    emit(SnackbarMessage(message, action))
}