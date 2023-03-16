package dev.chara.tasks.data.preference

actual class DataStorePath {
    actual fun get(fileName: String): String {
        val documentDirectory: NSURL = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory).path + "/$fileName"
    }
}