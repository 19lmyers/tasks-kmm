package dev.chara.tasks.shared.data.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.chara.tasks.shared.database.Database
import dev.chara.tasks.shared.database.DriverFactory
import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

class CacheDataSource(driverFactory: DriverFactory) {
    private val database = Database(driverFactory)

    fun getTaskLists() =
        database.taskListQueries.get().asFlow().mapToList(Dispatchers.IO).map { it.toModel() }

    fun getTaskListById(id: String) =
        database.taskListQueries.getById(id).asFlow().mapToOneOrNull(Dispatchers.IO).map {
            it?.toModel()
        }

    private fun getMaxListOrdinal() =
        database.taskListQueries.getMaxOrdinal().executeAsOneOrNull()?.MAX

    suspend fun insertTaskList(taskList: TaskList) =
        withContext(Dispatchers.IO) {
            database.taskListQueries.insert(
                id = taskList.id,
                title = taskList.title,
                color = taskList.color,
                icon = taskList.icon,
                description = taskList.description,
                show_index_numbers = taskList.showIndexNumbers,
                sort_type = taskList.sortType,
                sort_direction = taskList.sortDirection,
                date_created = taskList.dateCreated,
                last_modified = taskList.lastModified,
                ordinal =
                    if (taskList.ordinal == -1) {
                        getMaxListOrdinal()?.plus(1) ?: 0
                    } else {
                        taskList.ordinal.toLong()
                    }
            )
        }

    suspend fun updateTaskList(taskList: TaskList) =
        withContext(Dispatchers.IO) {
            database.taskListQueries.update(
                title = taskList.title,
                color = taskList.color,
                icon = taskList.icon,
                description = taskList.description,
                sort_type = taskList.sortType,
                sort_direction = taskList.sortDirection,
                show_index_numbers = taskList.showIndexNumbers,
                last_modified = taskList.lastModified,
                ordinal = taskList.ordinal.toLong(),
                id = taskList.id
            )
        }

    suspend fun reorderTaskList(
        listId: String,
        fromIndex: Int,
        toIndex: Int,
        lastModified: Instant
    ) =
        withContext(Dispatchers.IO) {
            database.taskListQueries.reorder(
                list_id = listId,
                ordinal = toIndex.toLong(),
                difference_sign = (fromIndex - toIndex).sign.toLong(),
                lower_bound = min(fromIndex, toIndex).toLong(),
                upper_bound = max(fromIndex, toIndex).toLong(),
                last_modified = lastModified
            )
        }

    suspend fun deleteTaskList(id: String) =
        withContext(Dispatchers.IO) { database.taskListQueries.delete(id) }

    fun getTasks() =
        database.taskQueries.get().asFlow().mapToList(Dispatchers.IO).map { it.toModel() }

    fun getStarredTasks() =
        database.taskQueries.getStarred().asFlow().mapToList(Dispatchers.IO).map { it.toModel() }

    fun getUpcomingTasks() =
        database.taskQueries.getUpcoming().asFlow().mapToList(Dispatchers.IO).map { it.toModel() }

    fun getOverdueTasks() =
        database.taskQueries.getOverdue().asFlow().mapToList(Dispatchers.IO).map { it.toModel() }

    fun getTaskById(id: String) =
        database.taskQueries.getById(id).asFlow().mapToOneOrNull(Dispatchers.IO).map {
            it?.toModel()
        }

    fun getTasksByList(
        listId: String,
        sortType: TaskList.SortType,
        sortDirection: TaskList.SortDirection,
        isCompleted: Boolean
    ) =
        when (sortType) {
                TaskList.SortType.ORDINAL ->
                    database.taskQueries.getByList_Ordinal(listId, isCompleted)
                TaskList.SortType.LABEL -> database.taskQueries.getByList_Label(listId, isCompleted)
                TaskList.SortType.DATE_CREATED ->
                    database.taskQueries.getByList_DateCreated(listId, isCompleted)
                TaskList.SortType.UPCOMING ->
                    database.taskQueries.getByList_Upcoming(listId, isCompleted)
                TaskList.SortType.STARRED ->
                    database.taskQueries.getByList_Starred(listId, isCompleted)
            }
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.toModel() }
            .let {
                if (
                    sortType != TaskList.SortType.ORDINAL &&
                        sortDirection == TaskList.SortDirection.DESCENDING
                ) {
                    it.map { tasks -> tasks.asReversed() }
                } else {
                    it
                }
            }

    fun getTaskCountForList(listId: String, isCompleted: Boolean) =
        database.taskQueries
            .getCountForList(listId, is_completed = isCompleted)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)

    private fun getMaxTaskOrdinal(listId: String) =
        database.taskQueries.getMaxOrdinal(listId).executeAsOneOrNull()?.MAX

    suspend fun insertTask(task: Task) =
        withContext(Dispatchers.IO) {
            database.taskQueries.insert(
                id = task.id,
                list_id = task.listId,
                label = task.label,
                is_completed = task.isCompleted,
                is_starred = task.isStarred,
                details = task.details,
                reminder_date = task.reminderDate,
                due_date = task.dueDate,
                date_created = task.dateCreated,
                last_modified = task.lastModified,
                ordinal =
                    if (task.ordinal == -1) {
                        getMaxTaskOrdinal(task.listId)?.plus(1) ?: 0
                    } else {
                        task.ordinal.toLong()
                    }
            )
        }

    suspend fun updateTask(task: Task) =
        withContext(Dispatchers.IO) {
            database.taskQueries.update(
                label = task.label,
                is_completed = task.isCompleted,
                is_starred = task.isStarred,
                details = task.details,
                reminder_date = task.reminderDate,
                due_date = task.dueDate,
                last_modified = task.lastModified,
                ordinal = task.ordinal.toLong(),
                id = task.id
            )
        }

    suspend fun moveTask(newListId: String, taskId: String, lastModified: Instant) =
        withContext(Dispatchers.IO) { database.taskQueries.move(newListId, lastModified, taskId) }

    suspend fun reorderTask(
        listId: String,
        taskId: String,
        fromIndex: Int,
        toIndex: Int,
        lastModified: Instant
    ) =
        withContext(Dispatchers.IO) {
            database.taskQueries.reorder(
                list_id = listId,
                task_id = taskId,
                ordinal = toIndex.toLong(),
                difference_sign = (fromIndex - toIndex).sign.toLong(),
                lower_bound = min(fromIndex, toIndex).toLong(),
                upper_bound = max(fromIndex, toIndex).toLong(),
                last_modified = lastModified
            )
        }

    suspend fun deleteTask(id: String) =
        withContext(Dispatchers.IO) { database.taskQueries.delete(id) }

    suspend fun deleteTasksByList(listId: String) =
        withContext(Dispatchers.IO) { database.taskQueries.deleteByList(listId) }

    suspend fun clearAndInsert(taskLists: Collection<TaskList>, tasks: Collection<Task>) =
        withContext(Dispatchers.IO) {
            database.transaction {
                database.taskQueries.deleteAll()
                database.taskListQueries.deleteAll()
                for (taskList in taskLists) {
                    database.taskListQueries.insert(
                        id = taskList.id,
                        title = taskList.title,
                        color = taskList.color,
                        icon = taskList.icon,
                        description = taskList.description,
                        show_index_numbers = taskList.showIndexNumbers,
                        sort_type = taskList.sortType,
                        sort_direction = taskList.sortDirection,
                        date_created = taskList.dateCreated,
                        last_modified = taskList.lastModified,
                        ordinal =
                            if (taskList.ordinal == -1) {
                                getMaxListOrdinal()?.plus(1) ?: 0
                            } else {
                                taskList.ordinal.toLong()
                            }
                    )
                }
                for (task in tasks) {
                    database.taskQueries.insert(
                        id = task.id,
                        list_id = task.listId,
                        label = task.label,
                        is_completed = task.isCompleted,
                        is_starred = task.isStarred,
                        details = task.details,
                        reminder_date = task.reminderDate,
                        due_date = task.dueDate,
                        date_created = task.dateCreated,
                        last_modified = task.lastModified,
                        ordinal =
                            if (task.ordinal == -1) {
                                getMaxTaskOrdinal(task.listId)?.plus(1) ?: 0
                            } else {
                                task.ordinal.toLong()
                            }
                    )
                }
            }
        }

    suspend fun deleteAll() =
        withContext(Dispatchers.IO) {
            database.transaction {
                database.taskQueries.deleteAll()
                database.taskListQueries.deleteAll()
            }
        }
}
