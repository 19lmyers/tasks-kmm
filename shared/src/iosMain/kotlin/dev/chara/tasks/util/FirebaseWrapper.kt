package dev.chara.tasks.util

import cocoapods.FirebaseCrashlytics.FIRCrashlytics
import cocoapods.FirebaseMessaging.FIRMessaging
import com.rickclephas.kmp.nserrorkt.asNSError

actual class FirebaseWrapper {
    actual companion object {
        actual suspend fun getMessagingToken(): String? = FIRMessaging.messaging().FCMToken()
        actual fun setUserId(userId: String) = FIRCrashlytics.crashlytics().setUserID(userId)
        actual fun log(msg: String) = FIRCrashlytics.crashlytics().log(msg)
        actual fun recordException(t: Throwable) {
            FIRCrashlytics.crashlytics().recordError(t.asNSError())
        }
    }
}