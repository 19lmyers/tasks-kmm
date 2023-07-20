package dev.chara.tasks.firebase

object Firebase {
    private lateinit var instance: FirebaseWrapper

    fun link(platformWrapper: FirebaseWrapper) {
        if (!this::instance.isInitialized) {
            instance = platformWrapper
        } else {
            throw IllegalStateException("Firebase has already been linked!")
        }
    }

    suspend fun getMessagingToken(): String? = instance.getMessagingToken()
    fun setUserId(userId: String) = instance.setUserId(userId)
    fun log(msg: String) = instance.log(msg)
    fun recordException(t: Throwable) = instance.recordException(t)
}

interface FirebaseWrapper {
    suspend fun getMessagingToken(): String?
    fun setUserId(userId: String)
    fun log(msg: String)
    fun recordException(t: Throwable)
}