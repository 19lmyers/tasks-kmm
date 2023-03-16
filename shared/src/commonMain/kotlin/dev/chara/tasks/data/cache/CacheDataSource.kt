package dev.chara.tasks.data.cache

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.db.SqlDriver
import dev.chara.tasks.data.cache.sql.CacheDatabase
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.util.time.SQLInstantFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import dev.chara.tasks.data.cache.sql.Task as CacheTask
import dev.chara.tasks.data.cache.sql.TaskList as CacheTaskList

expect class DriverFactory {
    fun create(): SqlDriver
}

private val instantAdapter = object : ColumnAdapter<Instant, String> {
    val formatter = SQLInstantFormatter()

    override fun decode(databaseValue: String): Instant = formatter.decode(databaseValue)
    override fun encode(value: Instant): String = formatter.encode(value)
}

class CacheDataSource(driverFactory: DriverFactory) {
    private val driver = driverFactory.create()

    private val database = CacheDatabase(
        driver,
        CacheTask.Adapter(
            reminder_dateAdapter = instantAdapter,
            due_dateAdapter = instantAdapter,
            date_createdAdapter = instantAdapter,
            last_modifiedAdapter = instantAdapter
        ),
        CacheTaskList.Adapter(
            colorAdapter = EnumColumnAdapter(),
            iconAdapter = EnumColumnAdapter(),
            sort_typeAdapter = EnumColumnAdapter(),
            sort_directionAdapter = EnumColumnAdapter(),
            date_createdAdapter = instantAdapter,
            last_modifiedAdapter = instantAdapter,
        )
    )

    fun getTaskLists() = database.taskListQueries
        .get()
        .asFlow()
        .mapToList(Dispatchers.Default)

    fun getPinnedTaskLists() = database.taskListQueries
        .getPinned()
        .asFlow()
        .mapToList(Dispatchers.Default)

    fun getTaskListById(id: String) = database.taskListQueries
        .getById(id)
        .asFlow()
        .mapToOneOrNull(Dispatchers.Default)

    fun insertTaskList(taskList: TaskList) = database.taskListQueries
        .insert(
            taskList.id,
            taskList.title,
            taskList.color,
            taskList.icon,
            taskList.description,
            taskList.isPinned,
            taskList.showIndexNumbers,
            taskList.sortType,
            taskList.sortDirection,
            taskList.dateCreated,
            taskList.lastModified
        )

    fun updateTaskList(taskList: TaskList) = database.taskListQueries
        .update(
            taskList.title,
            taskList.color,
            taskList.icon,
            taskList.description,
            taskList.isPinned,
            taskList.sortType,
            taskList.sortDirection,
            taskList.showIndexNumbers,
            taskList.lastModified,
            taskList.id
        )

    fun deleteTaskList(id: String) = database.taskListQueries
        .delete(id)

    fun getTasks() = database.taskQueries
        .get()
        .asFlow()
        .mapToList(Dispatchers.Default)

    fun getStarredTasks() = database.taskQueries
        .getStarred()
        .asFlow()
        .mapToList(Dispatchers.Default)

    fun getUpcomingTasks() = database.taskQueries
        .getUpcoming()
        .asFlow()
        .mapToList(Dispatchers.Default)

    fun getOverdueTasks() = database.taskQueries
        .getOverdue()
        .asFlow()
        .mapToList(Dispatchers.Default)

    fun getTaskById(id: String) = database.taskQueries
        .getById(id)
        .asFlow()
        .mapToOneOrNull(Dispatchers.Default)

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
        .mapToList(Dispatchers.Default)
        .let {
            if (sortType != TaskList.SortType.ORDINAL && sortDirection == TaskList.SortDirection.DESCENDING) {
                it.map { tasks ->
                    tasks.asReversed()
                }
            } else {
                it
            }
        }

    fun getTaskCountForList(listId: String) = database.taskQueries
        .getCountForList(listId)
        .asFlow()
        .mapToOneOrNull(Dispatchers.Default)

    private fun getMaxOrdinal(listId: String) = database.taskQueries
        .getMaxOrdinal(listId)
        .executeAsOneOrNull()
        ?.MAX


    fun insertTask(task: Task) = database.taskQueries
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

    fun updateTask(task: Task) = database.taskQueries
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

    fun moveTask(newListId: String, taskId: String, lastModified: Instant) =
        database.taskQueries.move(newListId, lastModified, taskId)

    fun reorderTask(
        listId: String,
        taskId: String,
        fromIndex: Int,
        toIndex: Int,
        lastModified: Instant
    ) {
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

    fun deleteTask(id: String) = database.taskQueries
        .delete(id)

    fun deleteTasksByList(listId: String) = database.taskQueries
        .deleteByList(listId)

    fun clearAndInsert(taskLists: Collection<TaskList>, tasks: Collection<Task>) =
        database.transaction {
            database.taskQueries.deleteAll()
            database.taskListQueries.deleteAll()
            for (taskList in taskLists) {
                insertTaskList(taskList)
            }
            for (task in tasks) {
                insertTask(task)
            }
        }

    fun deleteAll() = database.transaction {
        database.taskQueries.deleteAll()
        database.taskListQueries.deleteAll()
    }

    private fun getDatabaseVersion(): Int {
        val value = driver.executeQuery(
            identifier = null,
            sql = "PRAGMA user_version;",
            mapper = {
                it.next()
                it.getLong(0)
            },
            parameters = 0,
            binders = null
        ).value

        return value?.toInt() ?: 0
    }

}