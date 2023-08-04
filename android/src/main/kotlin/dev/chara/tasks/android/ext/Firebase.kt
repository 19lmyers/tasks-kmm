package dev.chara.tasks.android.ext

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dev.chara.tasks.shared.ext.FirebaseWrapper
import kotlinx.coroutines.tasks.await

class AndroidFirebaseWrapper : FirebaseWrapper {
    override suspend fun getMessagingToken(): String? = Firebase.messaging.token.await()
    override fun setUserId(userId: String) = Firebase.crashlytics.setUserId(userId)
    override fun log(msg: String) = Firebase.crashlytics.log(msg)
    override fun recordException(t: Throwable) = Firebase.crashlytics.recordException(t)
}