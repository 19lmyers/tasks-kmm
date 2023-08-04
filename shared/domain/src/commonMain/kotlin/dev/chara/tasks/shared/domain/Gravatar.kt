package dev.chara.tasks.shared.domain

import okio.ByteString.Companion.toByteString

object Gravatar {
    fun getUri(email: String): String {
        val trimmed = email.trim().lowercase()
        val byteString = trimmed.encodeToByteArray().toByteString()
        val hash = byteString.md5().hex()

        return "https://www.gravatar.com/avatar/$hash.png?d=identicon"
    }
}