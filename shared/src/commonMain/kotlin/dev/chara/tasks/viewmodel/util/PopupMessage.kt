package dev.chara.tasks.viewmodel.util

import dev.chara.tasks.data.DataResult
import kotlinx.coroutines.flow.MutableSharedFlow

data class PopupMessage(val text: String, val action: Action?) {
    data class Action(val text: String, val function: () -> Unit)
}

suspend fun <T> MutableSharedFlow<PopupMessage>.emitAsMessage(
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

suspend fun <T> MutableSharedFlow<PopupMessage>.emitAsMessage(
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

suspend fun <T> MutableSharedFlow<PopupMessage>.emitAsMessage(
    result: DataResult<T>,
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

private suspend fun <T> MutableSharedFlow<PopupMessage>.emit(
    result: DataResult<T>,
    defaultErrorMessage: String,
    successMessage: String? = null,
    action: PopupMessage.Action? = null,
) {
    result.fold(
        onSuccess = {
            successMessage?.let {
                PopupMessage(successMessage, action)
            }
        },
        onFailure = { errorMessage ->
            PopupMessage(errorMessage ?: defaultErrorMessage, action = null)
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