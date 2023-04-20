package dev.chara.tasks.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName
import dev.chara.tasks.data.cache.sql.Task as CacheTask

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

    val ordinal: Int = -1
)

@JvmName("toTaskModel")
fun Collection<CacheTask>.toModel() = map { it.toModel() }
fun CacheTask.toModel() = Task(
    this.id,
    this.list_id,

    this.label,
    this.is_completed,
    this.is_starred,

    this.details,
    this.reminder_date,
    this.due_date,

    this.date_created,
    this.last_modified,

    this.ordinal.toInt()
)
