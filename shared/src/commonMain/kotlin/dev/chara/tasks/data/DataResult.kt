package dev.chara.tasks.data

import io.github.aakira.napier.Napier
import kotlin.Result as KotlinResult

sealed class DataResult<T> {
    data class Success<T>(val result: T) : DataResult<T>()
    data class Failure<T>(val message: String?) : DataResult<T>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun onSuccess(onSuccess: (T) -> Unit): DataResult<T> {
        if (this is Success) {
            onSuccess(result)
        }
        return this
    }

    fun onFailure(onFailure: (String?) -> Unit): DataResult<T> {
        if (this is Failure) {
            onFailure(message)
        }
        return this
    }

    fun <R> fold(onSuccess: (T) -> R, onFailure: (String?) -> R): R =
        when (this) {
            is Success -> {
                onSuccess(result)
            }

            is Failure -> {
                onFailure(message)
            }
        }
}

suspend fun <T> KotlinResult<T>.toDataResult(
    onSuccess: (suspend (T) -> DataResult<T>) = {
        DataResult.Success(it)
    },
    onFailure: (suspend (Throwable) -> DataResult<T>) = {
        DataResult.Failure(it.message)
    },
) = fold(
    onSuccess = {
        onSuccess(it)
    },
    onFailure = {
        Napier.e(getLocalizedErrorMessage(it), it)

        onFailure(it)
    }
)

fun <T, R> DataResult<T>.with(value: R): DataResult<R> = fold(
    onSuccess = {
        DataResult.Success(value)
    },
    onFailure = {
        DataResult.Failure(it)
    }
)

fun getLocalizedErrorMessage(t: Throwable): String {
    return t.message ?: "An unknown error occurred" //TODO more user friendly errors
}