package dev.chara.tasks.shared.data.rest

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable data class Reorder(val fromIndex: Int, val toIndex: Int, val lastModified: Instant)
