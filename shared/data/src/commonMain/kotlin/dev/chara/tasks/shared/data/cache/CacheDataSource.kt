package dev.chara.tasks.shared.data.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.chara.tasks.shared.database.Database
import dev.chara.tasks.shared.database.DriverFactory
import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.TaskListPrefs
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
        database.taskListQueries.getByUser().asFlow().mapToList(Dispatchers.IO).map { it.toModel() }

    fun getListPrefs() =
        database.taskListQueries.getPrefsByUser().asFlow().mapToList(Dispatchers.IO).map {
            it.toModel()
        }

    fun getTaskListById(id: String) =
        database.taskListQueries.getById(id).asFlow().mapToOneOrNull(Dispatchers.IO).map {
            it?.toModel()
        }

    fun getListPrefsById(listId: String) =
        database.taskListQueries.getPrefsByIds(listId).asFlow().mapToOneOrNull(Dispatchers.IO).map {
            it?.toModel()
        }

    private fun getMaxListOrdinal() =
        database.taskListQueries.getMaxOrdinal().executeAsOneOrNull()?.MAX

    suspend fun insertTaskList(taskList: TaskList, prefs: TaskListPrefs) =
        withContext(Dispatchers.IO) {
            database.taskListQueries.insert(
                id = taskList.id,
                owner_id = taskList.ownerId,
                title = taskList.title,
                color = taskList.color,
                icon = taskList.icon,
                description = taskList.description,
                date_created = taskList.dateCreated,
                last_modified = taskList.lastModified,
                classifier_type = taskList.classifierType
            )

            database.taskListQueries.insertPrefs(
                list_id = taskList.id,
                show_index_numbers = prefs.showIndexNumbers,
                sort_type = prefs.sortType,
                sort_direction = prefs.sortDirection,
                last_modified = prefs.lastModified,
                ordinal =
                    if (prefs.ordinal == -1) {
                        getMaxListOrdinal()?.plus(1) ?: 0
                    } else {
                        prefs.ordinal.toLong()
                    },
            )
        }

    suspend fun updateTaskList(taskList: TaskList) =
        withContext(Dispatchers.IO) {
            database.taskListQueries.update(
                title = taskList.title,
                color = taskList.color,
                icon = taskList.icon,
                description = taskList.description,
                last_modified = taskList.lastModified,
                classifier_type = taskList.classifierType,
                id = taskList.id
            )
        }

    suspend fun updateListPrefs(prefs: TaskListPrefs) =
        withContext(Dispatchers.IO) {
            database.taskListQueries.updatePrefs(
                sort_type = prefs.sortType,
                sort_direction = prefs.sortDirection,
                show_index_numbers = prefs.showIndexNumbers,
                last_modified = prefs.lastModified,
                list_id = prefs.listId,
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
        sortType: TaskListPrefs.SortType,
        sortDirection: TaskListPrefs.SortDirection,
        isCompleted: Boolean
    ) =
        when (sortType) {
                TaskListPrefs.SortType.ORDINAL ->
                    database.taskQueries.getByList_Ordinal(listId, isCompleted)
                TaskListPrefs.SortType.LABEL ->
                    database.taskQueries.getByList_Label(listId, isCompleted)
                TaskListPrefs.SortType.CATEGORY ->
                    database.taskQueries.getByList_Category(listId, isCompleted)
                TaskListPrefs.SortType.DATE_CREATED ->
                    database.taskQueries.getByList_DateCreated(listId, isCompleted)
                TaskListPrefs.SortType.UPCOMING ->
                    database.taskQueries.getByList_Upcoming(listId, isCompleted)
                TaskListPrefs.SortType.STARRED ->
                    database.taskQueries.getByList_Starred(listId, isCompleted)
            }
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.toModel() }
            .let {
                if (
                    sortType != TaskListPrefs.SortType.ORDINAL &&
                        sortDirection == TaskListPrefs.SortDirection.DESCENDING
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
                    },
                category = task.category
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
                category = task.category,
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

    suspend fun clearAndInsert(
        taskLists: Collection<TaskList>,
        prefs: Collection<TaskListPrefs>,
        tasks: Collection<Task>
    ) =
        withContext(Dispatchers.IO) {
            database.transaction {
                database.taskQueries.deleteAll()
                database.taskListQueries.deleteAll()
                for (taskList in taskLists) {
                    database.taskListQueries.insert(
                        id = taskList.id,
                        owner_id = taskList.ownerId,
                        title = taskList.title,
                        color = taskList.color,
                        icon = taskList.icon,
                        description = taskList.description,
                        date_created = taskList.dateCreated,
                        last_modified = taskList.lastModified,
                        classifier_type = taskList.classifierType
                    )
                }
                for (pref in prefs) {
                    database.taskListQueries.insertPrefs(
                        list_id = pref.listId,
                        show_index_numbers = pref.showIndexNumbers,
                        sort_type = pref.sortType,
                        sort_direction = pref.sortDirection,
                        last_modified = pref.lastModified,
                        ordinal =
                            if (pref.ordinal == -1) {
                                getMaxListOrdinal()?.plus(1) ?: 0
                            } else {
                                pref.ordinal.toLong()
                            },
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
                            },
                        category = task.category
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
