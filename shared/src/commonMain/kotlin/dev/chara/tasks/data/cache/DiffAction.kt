package dev.chara.tasks.data.cache

import dev.chara.tasks.model.Task as ModelTask
import dev.chara.tasks.model.TaskList as ModelList

sealed class DiffAction {
    sealed class TaskList : DiffAction() {
        class Update(val taskList: ModelList) : TaskList()
    }

    sealed class Task : DiffAction() {
        class Create(val task: ModelTask) : Task()
        class Update(val task: ModelTask) : Task()
        class Move(val oldListID: String, val task: ModelTask) : Task()
    }
}