package dev.chara.tasks.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenPair(val access: String, val refresh: String)