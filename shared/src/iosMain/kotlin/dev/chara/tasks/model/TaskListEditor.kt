package dev.chara.tasks.model

import kotlinx.datetime.Instant

class TaskListEditor(private val taskList: TaskList) {
    private var title = taskList.title

    private var color = taskList.color
    private var icon = taskList.icon
    private var description = taskList.description

    private var isPinned = taskList.isPinned
    private var showIndexNumbers = taskList.showIndexNumbers

    private var sortType = taskList.sortType
    private var sortDirection = taskList.sortDirection

    private var dateCreated = taskList.dateCreated
    private var lastModified = taskList.lastModified

    fun title(value: String) = apply {
        title = value
    }

    fun color(value: TaskList.Color?) = apply {
        color = value
    }

    fun icon(value: TaskList.Icon?) = apply {
        icon = value
    }

    fun description(value: String?) = apply {
        description = value
    }

    fun isPinned(value: Boolean) = apply {
        isPinned = value
    }

    fun showIndexNumbers(value: Boolean) = apply {
        showIndexNumbers = value
    }

    fun sortType(value: TaskList.SortType) = apply {
        sortType = value
    }

    fun sortDirection(value: TaskList.SortDirection) = apply {
        sortDirection = value
    }

    fun dateCreated(value: Instant) = apply {
        dateCreated = value
    }

    fun lastModified(value: Instant) = apply {
        lastModified = value
    }

    fun build() = taskList.copy(
        title = title,

        color = color,
        icon = icon,
        description = description,

        isPinned = isPinned,
        showIndexNumbers = showIndexNumbers,

        sortType = sortType,
        sortDirection = sortDirection,

        dateCreated = dateCreated,
        lastModified = lastModified
    )
}

fun TaskList.edit() = TaskListEditor(this)