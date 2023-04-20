package dev.chara.tasks.model.board

import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList

data class PinnedList(
    val taskList: TaskList,
    val topTasks: List<Task>,
    val totalTaskCount: Int
)
