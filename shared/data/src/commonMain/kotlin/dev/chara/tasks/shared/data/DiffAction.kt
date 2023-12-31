package dev.chara.tasks.shared.data

import dev.chara.tasks.shared.model.Task as ModelTask
import dev.chara.tasks.shared.model.TaskList as ModelList
import dev.chara.tasks.shared.model.TaskListPrefs as ModelTaskListPrefs

internal sealed class DiffAction {
    sealed class TaskList : DiffAction() {
        class Update(val taskList: ModelList) : TaskList()
    }

    sealed class TaskListPrefs : DiffAction() {
        class Update(val prefs: ModelTaskListPrefs) : TaskListPrefs()
    }

    sealed class Task : DiffAction() {
        class Create(val task: ModelTask) : Task()

        class Update(val task: ModelTask) : Task()

        class Move(val oldListID: String, val task: ModelTask) : Task()
    }
}
