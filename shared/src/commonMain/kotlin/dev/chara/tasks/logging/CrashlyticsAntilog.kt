package dev.chara.tasks.logging

import dev.chara.tasks.util.FirebaseWrapper
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

class CrashlyticsAntilog : Antilog() {

    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        message?.let {
            if (tag != null) {
                FirebaseWrapper.log("${priority.name} $tag: $message")
            } else {
                FirebaseWrapper.log("${priority.name}: $message")
            }
        }

        throwable?.let {
            FirebaseWrapper.recordException(it)
        }
    }
}