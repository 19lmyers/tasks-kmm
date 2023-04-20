package dev.chara.tasks.data

interface DataError

sealed class ApiError(val message: String?) : DataError {
    class InvalidQuery(message: String?) : ApiError(message)
    class OtherServerError(message: String?) : ApiError(message)
}

class ClientError(val exception: Throwable) : DataError