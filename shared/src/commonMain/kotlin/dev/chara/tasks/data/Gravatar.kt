package dev.chara.tasks.data

import okio.ByteString.Companion.toByteString

// NOTE: This breaks Studio Preview??
fun getGravatarUrl(email: String): String {
    val trimmed = email.trim().lowercase()
    val byteString = trimmed.encodeToByteArray().toByteString()
    val hash = byteString.md5().hex()

    return "https://www.gravatar.com/avatar/$hash.png?d=identicon"
}
