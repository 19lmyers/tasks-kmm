package dev.chara.tasks.shared.data.cache

import dev.chara.tasks.shared.database.sql.Task as SqlTask
import dev.chara.tasks.shared.database.sql.TaskList as SqlTaskList
import dev.chara.tasks.shared.database.sql.TaskListPrefs as SqlTaskListPrefs
import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.TaskListPrefs
import kotlin.jvm.JvmName

@JvmName("toTaskModel") fun Collection<SqlTask>.toModel() = map { it.toModel() }

fun SqlTask.toModel() =
    Task(
        id = this.id,
        listId = this.list_id,
        label = this.label,
        isCompleted = this.is_completed,
        isStarred = this.is_starred,
        details = this.details,
        reminderDate = this.reminder_date,
        dueDate = this.due_date,
        dateCreated = this.date_created,
        lastModified = this.last_modified,
        ordinal = this.ordinal.toInt(),
        category = this.category
    )

@JvmName("toTaskListModel") fun Collection<SqlTaskList>.toModel() = map { it.toModel() }

fun SqlTaskList.toModel() =
    TaskList(
        id = this.id,
        ownerId = this.owner_id,
        title = this.title,
        color = this.color,
        icon = this.icon,
        description = this.description,
        dateCreated = this.date_created,
        lastModified = this.last_modified,
        classifierType = this.classifier_type
    )

@JvmName("toTaskListPrefsModel") fun Collection<SqlTaskListPrefs>.toModel() = map { it.toModel() }

fun SqlTaskListPrefs.toModel() =
    TaskListPrefs(
        listId = this.list_id,
        showIndexNumbers = this.show_index_numbers,
        sortType = this.sort_type,
        sortDirection = this.sort_direction,
        ordinal = this.ordinal.toInt(),
        lastModified = this.last_modified
    )
