package dev.chara.tasks.shared.data.preference

import android.content.Context

actual class DataStorePath(private val context: Context) {
    actual fun get(fileName: String): String {
        return context.filesDir.resolve(fileName).absolutePath
    }
}
