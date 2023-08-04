package dev.chara.tasks.shared.ext

interface FirebaseWrapper {
    suspend fun getMessagingToken(): String?
    fun setUserId(userId: String)
    fun log(msg: String)
    fun recordException(t: Throwable)
}