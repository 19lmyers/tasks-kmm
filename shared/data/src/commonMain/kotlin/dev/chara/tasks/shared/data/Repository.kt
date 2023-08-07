package dev.chara.tasks.shared.data

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.binding.binding
import com.github.michaelbull.result.onSuccess
import dev.chara.tasks.shared.data.cache.CacheDataSource
import dev.chara.tasks.shared.data.preference.PreferenceDataSource
import dev.chara.tasks.shared.data.rest.RestDataSource
import dev.chara.tasks.shared.ext.FirebaseWrapper
import dev.chara.tasks.shared.ext.WidgetManager
import dev.chara.tasks.shared.model.Profile
import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.board.BoardList
import dev.chara.tasks.shared.model.board.BoardSection
import dev.chara.tasks.shared.model.preference.Theme
import dev.chara.tasks.shared.model.preference.ThemeVariant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/** TODO: Refactor into multiple Repositories Move multi-Repository logic into Use Cases */
class Repository(
    private val cacheDataSource: CacheDataSource,
    private val preferenceDataSource: PreferenceDataSource,
    private val restDataSource: RestDataSource,
    private val firebase: FirebaseWrapper,
    private val widgetManager: WidgetManager
) {
    fun isUserAuthenticated() = preferenceDataSource.getUserProfile().map { it != null }

    suspend fun authenticateUser(userId: String, password: String) = binding {
        val tokenPair = restDataSource.authenticateUser(userId, password).bind()

        preferenceDataSource.setUserProfile(
            Profile(id = "", email = userId, displayName = "", profilePhotoUri = null)
        )
        preferenceDataSource.setApiTokens(tokenPair)

        firebase.setUserId(userId)
        val fcmToken = firebase.getMessagingToken()
        if (fcmToken != null) {
            linkFCMToken(fcmToken).bind()
        }

        refreshUserProfile().bind()
        refreshCache().bind()
    }

    suspend fun createUser(email: String, displayName: String, password: String) =
        restDataSource.createUser(email, displayName, password).andThen {
            authenticateUser(email, password)
        }

    suspend fun changeEmail(newEmail: String) = restDataSource.changeEmail(newEmail)

    suspend fun requestVerifyEmailResend() = restDataSource.requestVerifyEmailResend()

    suspend fun changePassword(currentPassword: String, newPassword: String) =
        restDataSource.changePassword(currentPassword, newPassword)

    suspend fun verifyEmail(verifyToken: String, email: String) =
        restDataSource.verifyEmail(verifyToken, email).andThen { refreshUserProfile() }

    suspend fun requestPasswordResetEmail(email: String) =
        restDataSource.requestPasswordResetEmail(email)

    suspend fun resetPassword(resetToken: String, newPassword: String) =
        restDataSource.resetPassword(resetToken, newPassword)

    suspend fun logout() = binding {
        preferenceDataSource.clearAuthFields()
        cacheDataSource.deleteAll()

        firebase.setUserId("")
        firebase.getMessagingToken()?.apply { restDataSource.invalidateFcmToken(this).bind() }
    }

    fun getUserProfile() = preferenceDataSource.getUserProfile()

    suspend fun updateUserProfile(profile: Profile): Result<Unit, DataError> {
        preferenceDataSource.setUserProfile(profile)
        return restDataSource.updateUserProfile(profile)
    }

    suspend fun updateUserProfilePhoto(photo: ByteArray) =
        restDataSource.updateUserProfilePhoto(photo).andThen {
            refreshUserProfile()
            Ok(Unit)
        }

    suspend fun refresh(): Result<Unit, DataError> {
        refreshUserProfile()
        return refreshCache()
    }

    private suspend fun refreshUserProfile() =
        restDataSource.getUserProfile().andThen { profile ->
            preferenceDataSource.setUserProfile(profile)
            Ok(Unit)
        }

    private suspend fun refreshCache(
        skipIds: MutableList<String> = mutableListOf()
    ): Result<Unit, DataError> = binding {
        val taskLists = restDataSource.getLists().bind()

        val tasks = mutableListOf<Task>()
        for (taskList in taskLists) {
            val tasksForList = restDataSource.getTasks(taskList.id).bind()
            tasks.addAll(tasksForList)
        }

        val freshLists = taskLists.associateBy { it.id }.toMutableMap()

        val freshTasks = tasks.associateBy { it.id }.toMutableMap()

        val staleLists = cacheDataSource.getTaskLists().first()

        val staleTasks = cacheDataSource.getTasks().first()

        // This represents the actions to take on the server to get it "up to speed"
        val serverDiff = mutableListOf<DiffAction>()

        // Loop over stale task lists
        for (staleList in staleLists) {
            // If we have a match from the server...
            if (freshLists.containsKey(staleList.id)) {
                val freshList = freshLists.remove(staleList.id)!!

                // If the server's task list is older...
                if (freshList.lastModified < staleList.lastModified) {
                    // Update the server's value with our newer value
                    serverDiff.add(DiffAction.TaskList.Update(staleList))
                }
            }
            // Otherwise, it's been deleted, so we can ignore it
        }

        // Loop over stale tasks
        for (staleTask in staleTasks) {
            // If we haven't previously dealt with this task...
            if (!skipIds.contains(staleTask.id)) {
                // Otherwise, if we have a match from the server...
                if (freshTasks.containsKey(staleTask.id)) {
                    val freshTask = freshTasks.remove(staleTask.id)!!

                    // If the server's task is older...
                    if (freshTask.lastModified < staleTask.lastModified) {
                        // If the task has been moved, move it on the server
                        if (freshTask.listId != staleTask.listId) {
                            serverDiff.add(DiffAction.Task.Move(freshTask.listId, staleTask))
                        }
                        // Update the server's value
                        serverDiff.add(DiffAction.Task.Update(staleTask))
                    }
                }
                // Otherwise, if the ID is a temp ID...
                else if (staleTask.isDirty()) {
                    // Send it to the server
                    serverDiff.add(DiffAction.Task.Create(staleTask))
                }
            }
            // Otherwise, it's been deleted, so we can ignore it
        }

        if (serverDiff.isNotEmpty()) {
            for (action in serverDiff) {
                val result =
                    when (action) {
                        is DiffAction.TaskList.Update -> {
                            restDataSource.updateList(action.taskList.id, action.taskList)
                        }
                        is DiffAction.Task.Create -> {
                            restDataSource.createTask(action.task.listId, action.task).onSuccess {
                                skipIds.add(action.task.id)
                            }
                        }
                        is DiffAction.Task.Update -> {
                            restDataSource.updateTask(
                                action.task.listId,
                                action.task.id,
                                action.task
                            )
                        }
                        is DiffAction.Task.Move -> {
                            restDataSource.moveTask(
                                action.oldListID,
                                action.task.listId,
                                action.task.id,
                                Clock.System.now()
                            )
                        }
                    }

                result.bind()
            }

            refreshCache(skipIds)
        } else {
            cacheDataSource.clearAndInsert(taskLists, tasks)
            widgetManager.update()
            Ok(Unit)
        }
    }

    fun getLists() = cacheDataSource.getTaskLists()

    fun getListById(listId: String) = cacheDataSource.getTaskListById(listId)

    suspend fun createList(taskList: TaskList) =
        restDataSource.createList(taskList).onSuccess { refreshCache() }

    suspend fun updateList(listId: String, taskList: TaskList): Result<Unit, DataError> {
        cacheDataSource.updateTaskList(taskList)
        widgetManager.update()
        return restDataSource.updateList(listId, taskList)
    }

    suspend fun reorderList(
        listId: String,
        fromIndex: Int,
        toIndex: Int,
        lastModified: Instant
    ): Result<Unit, DataError> {
        cacheDataSource.reorderTaskList(listId, fromIndex, toIndex, lastModified)
        widgetManager.update()
        return restDataSource.reorderList(listId, fromIndex, toIndex, lastModified)
    }

    suspend fun deleteList(id: String) =
        restDataSource.deleteList(id).onSuccess {
            cacheDataSource.deleteTasksByList(id)
            cacheDataSource.deleteTaskList(id)
            widgetManager.update()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTasksByList(listId: String, isCompleted: Boolean) =
        getListById(listId).flatMapLatest { taskList ->
            if (taskList == null) {
                return@flatMapLatest flowOf(emptyList())
            }
            cacheDataSource.getTasksByList(
                listId,
                taskList.sortType,
                taskList.sortDirection,
                isCompleted
            )
        }

    fun getTaskById(id: String) = cacheDataSource.getTaskById(id)

    suspend fun createTask(listId: String, task: Task): Result<Unit, DataError> {
        cacheDataSource.insertTask(task.copy(id = newDirtyId(), listId = listId))
        return refreshCache()
    }

    suspend fun updateTask(listId: String, taskId: String, task: Task): Result<Unit, DataError> {
        cacheDataSource.updateTask(task)
        widgetManager.update()
        return restDataSource.updateTask(listId, taskId, task)
    }

    suspend fun moveTask(
        oldListId: String,
        newListId: String,
        taskId: String,
        lastModified: Instant
    ): Result<Unit, DataError> {
        cacheDataSource.moveTask(newListId, taskId, lastModified)
        widgetManager.update()
        return restDataSource.moveTask(oldListId, newListId, taskId, lastModified)
    }

    suspend fun reorderTask(
        listId: String,
        taskId: String,
        fromIndex: Int,
        toIndex: Int,
        lastModified: Instant
    ): Result<Unit, DataError> {
        cacheDataSource.reorderTask(listId, taskId, fromIndex, toIndex, lastModified)
        widgetManager.update()
        return restDataSource.reorderTask(listId, taskId, fromIndex, toIndex, lastModified)
    }

    suspend fun deleteTask(listId: String, taskId: String) =
        restDataSource.deleteTask(listId, taskId).onSuccess {
            cacheDataSource.deleteTask(taskId)
            widgetManager.update()
        }

    suspend fun clearCompletedTasks(listId: String) =
        restDataSource.clearCompletedTasks(listId).andThen { refreshCache() }

    suspend fun linkFCMToken(fcmToken: String) = restDataSource.linkFCMToken(fcmToken)

    fun getAppTheme() = preferenceDataSource.getAppTheme()

    suspend fun setAppTheme(theme: Theme) = preferenceDataSource.setAppTheme(theme)

    fun getAppThemeVariant() = preferenceDataSource.getAppThemeVariant()

    suspend fun setAppThemeVariant(variant: ThemeVariant) =
        preferenceDataSource.setAppThemeVariant(variant)

    fun getEnabledBoardSections() =
        preferenceDataSource.getDisabledBoardSections().map { disabledSections ->
            val enabledSections = BoardSection.Type.values().toMutableList()
            enabledSections.removeAll(disabledSections.map { BoardSection.Type.valueOf(it) })
            enabledSections
        }

    suspend fun setEnabledForBoardSection(boardSection: BoardSection.Type, enabled: Boolean) =
        preferenceDataSource.setDisabledForBoardSection(boardSection, !enabled)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getBoardSections() =
        getEnabledBoardSections().flatMapLatest { enabledSections ->
            if (enabledSections.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            val sectionFlows = mutableListOf<Flow<BoardSection>>()

            for (it in enabledSections) {
                sectionFlows.add(getBoardSection(it))
            }

            combine(sectionFlows) { sections -> sections.toList().filter { it.tasks.isNotEmpty() } }
        }

    private fun getBoardSection(type: BoardSection.Type) =
        when (type) {
            BoardSection.Type.OVERDUE -> {
                cacheDataSource.getOverdueTasks().map { BoardSection(type, it) }
            }
            BoardSection.Type.STARRED -> {
                cacheDataSource.getStarredTasks().map { BoardSection(type, it) }
            }
            BoardSection.Type.UPCOMING -> {
                cacheDataSource.getUpcomingTasks().map { BoardSection(type, it) }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getBoardLists() =
        cacheDataSource.getTaskLists().flatMapLatest { taskLists ->
            if (taskLists.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            val boardLists = mutableListOf<Flow<BoardList>>()

            for (list in taskLists) {
                boardLists.add(getBoardList(list))
            }

            combine(boardLists) { lists -> lists.toList() }
        }

    private fun getBoardList(taskList: TaskList) =
        combine(
            cacheDataSource.getTasksByList(
                taskList.id,
                taskList.sortType,
                taskList.sortDirection,
                isCompleted = false
            ),
            cacheDataSource.getTaskCountForList(taskList.id, false),
            cacheDataSource.getTaskCountForList(taskList.id, true)
        ) { topTasks, currentTaskCount, completedTaskCount ->
            BoardList(
                taskList,
                topTasks.take(NUM_TOP_TASKS),
                currentTaskCount?.toInt() ?: 0,
                completedTaskCount?.toInt() ?: 0
            )
        }

    companion object {
        const val NUM_TOP_TASKS = 3
    }
}
