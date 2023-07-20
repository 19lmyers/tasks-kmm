package dev.chara.tasks.logging

import dev.chara.tasks.firebase.Firebase
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
                Firebase.log("${priority.name} $tag: $message")
            } else {
                Firebase.log("${priority.name}: $message")
            }
        }

        throwable?.let {
            Firebase.recordException(it)
        }
    }
}