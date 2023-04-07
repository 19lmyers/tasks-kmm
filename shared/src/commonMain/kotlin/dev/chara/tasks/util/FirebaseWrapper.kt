package dev.chara.tasks.util

expect class FirebaseWrapper {
    companion object {
        suspend fun getMessagingToken(): String?
        fun setUserId(userId: String)
        fun log(msg: String)
        fun recordException(t: Throwable)
    }
}