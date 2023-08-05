package dev.chara.tasks.shared.model.board

import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList

data class BoardList(
    val taskList: TaskList,
    val topTasks: List<Task>,
    val currentTaskCount: Int,
    val completedTaskCount: Int
)
