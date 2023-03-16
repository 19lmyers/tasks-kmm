package dev.chara.tasks.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Move(val newListId: String, val lastModified: Instant)

@Serializable
data class Reorder(val fromIndex: Int, val toIndex: Int, val lastModified: Instant)