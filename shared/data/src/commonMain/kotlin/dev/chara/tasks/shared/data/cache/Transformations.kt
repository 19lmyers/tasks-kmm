package dev.chara.tasks.shared.data.cache

import dev.chara.tasks.shared.database.sql.Task as SqlTask
import dev.chara.tasks.shared.database.sql.TaskList as SqlTaskList
import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList
import kotlin.jvm.JvmName

@JvmName("toTaskModel") fun Collection<SqlTask>.toModel() = map { it.toModel() }

fun SqlTask.toModel() =
    Task(
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

@JvmName("toTaskListModel") fun Collection<SqlTaskList>.toModel() = map { it.toModel() }

fun SqlTaskList.toModel() =
    TaskList(
        this.id,
        this.title,
        this.color,
        this.icon,
        this.description,
        this.show_index_numbers,
        this.sort_type,
        this.sort_direction,
        this.date_created,
        this.last_modified
    )
