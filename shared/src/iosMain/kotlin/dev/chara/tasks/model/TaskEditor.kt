package dev.chara.tasks.model

import kotlinx.datetime.Instant

class TaskEditor(private val task: Task) {
    private var listId = task.listId

    private var label = task.label
    private var isCompleted = task.isCompleted
    private var isStarred = task.isStarred

    private var details = task.details
    private var reminderDate = task.reminderDate
    private var dueDate = task.dueDate

    private var dateCreated = task.dateCreated
    private var lastModified = task.lastModified

    private var ordinal = task.ordinal

    fun listId(value: String) = apply {
        listId = value
    }

    fun label(value: String) = apply {
        label = value
    }

    fun isCompleted(value: Boolean) = apply {
        isCompleted = value
    }

    fun isStarred(value: Boolean) = apply {
        isStarred = value
    }

    fun details(value: String?) = apply {
        details = value
    }

    fun reminderDate(value: Instant?) = apply {
        reminderDate = value
    }

    fun dueDate(value: Instant?) = apply {
        dueDate = value
    }

    fun dateCreated(value: Instant) = apply {
        dateCreated = value
    }

    fun lastModified(value: Instant) = apply {
        lastModified = value
    }

    fun ordinal(value: Int) = apply {
        ordinal = value
    }

    fun build() = task.copy(
        listId = listId,

        label = label,
        isCompleted = isCompleted,
        isStarred = isStarred,

        details = details,
        reminderDate = reminderDate,
        dueDate = dueDate,

        dateCreated = dateCreated,
        lastModified = lastModified,
        ordinal = ordinal
    )
}

fun Task.edit() = TaskEditor(this)