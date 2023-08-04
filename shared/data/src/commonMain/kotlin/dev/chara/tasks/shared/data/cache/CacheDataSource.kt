package dev.chara.tasks.shared.data.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.chara.tasks.shared.database.Database
import dev.chara.tasks.shared.database.DriverFactory
import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

class CacheDataSource(driverFactory: DriverFactory) {
    private val database = Database(driverFactory)

    fun getTaskLists() = database.taskListQueries
        .get()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { it.toModel() }

    fun getTaskListById(id: String) = database.taskListQueries
        .getById(id)
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { it?.toModel() }

    suspend fun insertTaskList(taskList: TaskList) = withContext(Dispatchers.IO) {
        database.taskListQueries
            .insert(
                taskList.id,
                taskList.title,
                taskList.color,
                taskList.icon,
                taskList.description,
                taskList.showIndexNumbers,
                taskList.sortType,
                taskList.sortDirection,
                taskList.dateCreated,
                taskList.lastModified
            )
    }

    suspend fun updateTaskList(taskList: TaskList) = withContext(Dispatchers.IO) {
        database.taskListQueries
            .update(
                taskList.title,
                taskList.color,
                taskList.icon,
                taskList.description,
                taskList.sortType,
                taskList.sortDirection,
                taskList.showIndexNumbers,
                taskList.lastModified,
                taskList.id
            )
    }

    suspend fun deleteTaskList(id: String) = withContext(Dispatchers.IO) {
        database.taskListQueries
            .delete(id)
    }

    fun getTasks() = database.taskQueries
        .get()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { it.toModel() }

    fun getStarredTasks() = database.taskQueries
        .getStarred()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { it.toModel() }

    fun getUpcomingTasks() = database.taskQueries
        .getUpcoming()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { it.toModel() }

    fun getOverdueTasks() = database.taskQueries
        .getOverdue()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { it.toModel() }

    fun getTaskById(id: String) = database.taskQueries
        .getById(id)
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { it?.toModel() }

    fun getTasksByList(
        listId: String,
        sortType: TaskList.SortType,
        sortDirection: TaskList.SortDirection,
        isCompleted: Boolean
    ) = when (sortType) {
        TaskList.SortType.ORDINAL -> database.taskQueries.getByList_Ordinal(listId, isCompleted)
        TaskList.SortType.LABEL -> database.taskQueries.getByList_Label(listId, isCompleted)
        TaskList.SortType.DATE_CREATED -> database.taskQueries.getByList_DateCreated(
            listId,
            isCompleted
        )

        TaskList.SortType.UPCOMING -> database.taskQueries.getByList_Upcoming(listId, isCompleted)
        TaskList.SortType.STARRED -> database.taskQueries.getByList_Starred(listId, isCompleted)
    }.asFlow()
        .mapToList(Dispatchers.IO)
        .map { it.toModel() }
        .let {
            if (sortType != TaskList.SortType.ORDINAL && sortDirection == TaskList.SortDirection.DESCENDING) {
                it.map { tasks ->
                    tasks.asReversed()
                }
            } else {
                it
            }
        }

    fun getTaskCountForList(listId: String, isCompleted: Boolean) = database.taskQueries
        .getCountForList(listId, is_completed = isCompleted)
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)

    private fun getMaxOrdinal(listId: String) = database.taskQueries
        .getMaxOrdinal(listId)
        .executeAsOneOrNull()
        ?.MAX


    suspend fun insertTask(task: Task) = withContext(Dispatchers.IO) {
        database.taskQueries
            .insert(
                task.id,
                task.listId,
                task.label,
                task.isCompleted,
                task.isStarred,
                task.details,
                task.reminderDate,
                task.dueDate,
                task.dateCreated,
                task.lastModified,
                if (task.ordinal == -1) {
                    getMaxOrdinal(task.listId)?.plus(1) ?: 0
                } else {
                    task.ordinal.toLong()
                }
            )
    }

    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        database.taskQueries
            .update(
                task.label,
                task.isCompleted,
                task.isStarred,
                task.details,
                task.reminderDate,
                task.dueDate,
                task.lastModified,
                task.ordinal.toLong(),
                task.id
            )
    }

    suspend fun moveTask(newListId: String, taskId: String, lastModified: Instant) =
        withContext(Dispatchers.IO) {
            database.taskQueries.move(newListId, lastModified, taskId)
        }

    suspend fun reorderTask(
        listId: String,
        taskId: String,
        fromIndex: Int,
        toIndex: Int,
        lastModified: Instant
    ) = withContext(Dispatchers.IO) {
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

    suspend fun deleteTask(id: String) = withContext(Dispatchers.IO) {
        database.taskQueries
            .delete(id)
    }

    suspend fun deleteTasksByList(listId: String) = withContext(Dispatchers.IO) {
        database.taskQueries
            .deleteByList(listId)
    }

    suspend fun clearAndInsert(taskLists: Collection<TaskList>, tasks: Collection<Task>) =
        withContext(Dispatchers.IO) {
            database.transaction {
                database.taskQueries.deleteAll()
                database.taskListQueries.deleteAll()
                for (taskList in taskLists) {
                    database.taskListQueries
                        .insert(
                            taskList.id,
                            taskList.title,
                            taskList.color,
                            taskList.icon,
                            taskList.description,
                            taskList.showIndexNumbers,
                            taskList.sortType,
                            taskList.sortDirection,
                            taskList.dateCreated,
                            taskList.lastModified
                        )
                }
                for (task in tasks) {
                    database.taskQueries
                        .insert(
                            task.id,
                            task.listId,
                            task.label,
                            task.isCompleted,
                            task.isStarred,
                            task.details,
                            task.reminderDate,
                            task.dueDate,
                            task.dateCreated,
                            task.lastModified,
                            if (task.ordinal == -1) {
                                getMaxOrdinal(task.listId)?.plus(1) ?: 0
                            } else {
                                task.ordinal.toLong()
                            }
                        )
                }
            }
        }

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        database.transaction {
            database.taskQueries.deleteAll()
            database.taskListQueries.deleteAll()
        }
    }
}