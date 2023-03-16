package dev.chara.tasks.util.firebase

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await

actual class FirebaseWrapper {
    actual companion object {
        actual suspend fun getMessagingToken(): String = Firebase.messaging.token.await()
        actual fun setUserId(userId: String) = Firebase.crashlytics.setUserId(userId)
        actual fun log(msg: String) = Firebase.crashlytics.log(msg)
        actual fun recordException(t: Throwable) = Firebase.crashlytics.recordException(t)
    }
}