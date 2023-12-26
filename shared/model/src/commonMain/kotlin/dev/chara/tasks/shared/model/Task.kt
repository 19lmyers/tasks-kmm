package dev.chara.tasks.shared.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String,
    val listId: String,
    val label: String,
    val isCompleted: Boolean = false,
    val isStarred: Boolean = false,
    val details: String? = null,
    val reminderDate: Instant? = null,
    val dueDate: Instant? = null,
    val dateCreated: Instant = Clock.System.now(),
    val lastModified: Instant = Clock.System.now(),
    val ordinal: Int = -1,
    val category: String? = null
)
