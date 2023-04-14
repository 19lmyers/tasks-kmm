package dev.chara.tasks.data

import dev.chara.tasks.data.cache.CacheDataSource
import dev.chara.tasks.data.cache.DiffAction
import dev.chara.tasks.data.cache.isDirty
import dev.chara.tasks.data.cache.newDirtyId
import dev.chara.tasks.data.preference.PreferenceDataSource
import dev.chara.tasks.data.rest.RestDataSource
import dev.chara.tasks.model.BoardSection
import dev.chara.tasks.model.PinnedList
import dev.chara.tasks.model.Profile
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.model.Theme
import dev.chara.tasks.model.toModel
import dev.chara.tasks.util.FirebaseWrapper
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class Repository(
    private val cacheDataSource: CacheDataSource,
    private val preferenceDataSource: PreferenceDataSource,
    private val restDataSource: RestDataSource
) {
    fun isUserAuthenticated() = preferenceDataSource.getUserProfile().map { it != null }

    suspend fun authenticateUser(userId: String, password: String) =
        restDataSource.authenticateUser(userId, password).toDataResult(
            onSuccess = { tokenPair ->
                preferenceDataSource.setUserProfile(
                    Profile(
                        email = userId,
                        displayName = "",
                        profilePhotoUri = null
                    )
                )
                preferenceDataSource.setApiTokens(tokenPair)

                FirebaseWrapper.setUserId(userId)

                val fcmToken = FirebaseWrapper.getMessagingToken()
                if (fcmToken != null) {
                    linkFCMToken(fcmToken)
                }

                refreshUserProfile()
                refreshCache().with(tokenPair)
            }
        )

    suspend fun createUser(email: String, displayName: String, password: String) =
        restDataSource.createUser(email, displayName, password).toDataResult(
            onSuccess = {
                authenticateUser(email, password).with(Unit)
            }
        )

    suspend fun changePassword(currentPassword: String, newPassword: String) =
        restDataSource.changePassword(currentPassword, newPassword).toDataResult()

    suspend fun requestPasswordResetEmail(email: String) =
        restDataSource.requestPasswordResetEmail(email).toDataResult()

    suspend fun resetPassword(resetToken: String, newPassword: String) =
        restDataSource.resetPassword(resetToken, newPassword).toDataResult()

    suspend fun logout() {
        FirebaseWrapper.getMessagingToken()?.apply {
            restDataSource.invalidateFcmToken(this)
        }
        preferenceDataSource.clearAuthFields()
        cacheDataSource.deleteAll()

        FirebaseWrapper.setUserId("")
    }

    fun getUserProfile() = preferenceDataSource.getUserProfile()

    suspend fun updateUserProfile(profile: Profile): DataResult<Unit> {
        preferenceDataSource.setUserProfile(profile)
        return restDataSource.updateUserProfile(profile).toDataResult()
    }

    suspend fun updateUserProfilePhoto(photo: ByteArray): DataResult<Unit> {
        return restDataSource.updateUserProfilePhoto(photo).toDataResult(
            onSuccess = {
                refreshUserProfile()
                DataResult.Success(Unit)
            }
        )
    }

    suspend fun refresh(): DataResult<Unit> {
        refreshUserProfile()
        return refreshCache()
    }

    private suspend fun refreshUserProfile() = restDataSource.getUserProfile().toDataResult(
        onSuccess = { profile ->
            preferenceDataSource.setUserProfile(profile)
            DataResult.Success(profile)
        }
    ).with(Unit)

    private suspend fun refreshCache(skipIds: MutableList<String> = mutableListOf()): DataResult<Unit> {
        val listResult = restDataSource.getLists()

        return listResult.fold(
            onFailure = {
                Napier.e(getLocalizedErrorMessage(it), it)
                DataResult.Failure(it.message)
            },
            onSuccess = { taskLists ->
                val tasks = mutableListOf<Task>()

                for (taskList in taskLists) {
                    val taskResult = restDataSource.getTasks(taskList.id)
                    taskResult.fold(
                        onSuccess = {
                            tasks.addAll(it)
                        },
                        onFailure = {
                            Napier.e(getLocalizedErrorMessage(it), it)
                            return DataResult.Failure(it.message)
                        }
                    )
                }

                val freshLists = taskLists
                    .associateBy { it.id }
                    .toMutableMap()

                val freshTasks = tasks
                    .associateBy { it.id }
                    .toMutableMap()

                val staleLists = cacheDataSource.getTaskLists()
                    .first()
                    .toModel()

                val staleTasks = cacheDataSource.getTasks()
                    .first()
                    .toModel()

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
                                    serverDiff.add(
                                        DiffAction.Task.Move(
                                            freshTask.listId,
                                            staleTask
                                        )
                                    )
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
                        val result = when (action) {
                            is DiffAction.TaskList.Update -> {
                                restDataSource.updateList(action.taskList.id, action.taskList)
                            }

                            is DiffAction.Task.Create -> {
                                restDataSource.createTask(action.task.listId, action.task)
                                    .onSuccess {
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

                        result.onFailure {
                            Napier.e(getLocalizedErrorMessage(it), it)
                            return DataResult.Failure(it.message)
                        }
                    }

                    return refreshCache(skipIds)
                } else {
                    cacheDataSource.clearAndInsert(taskLists, tasks)
                    return DataResult.Success(Unit)
                }
            }
        )
    }

    fun getLists() = cacheDataSource.getTaskLists()
        .map { it.toModel() }

    fun getListById(listId: String) = cacheDataSource.getTaskListById(listId)
        .map { it?.toModel() }

    suspend fun createList(taskList: TaskList) = restDataSource.createList(taskList)
        .onSuccess { refreshCache() }.toDataResult()

    suspend fun updateList(listId: String, taskList: TaskList): DataResult<Unit> {
        cacheDataSource.updateTaskList(taskList)
        return restDataSource.updateList(listId, taskList).toDataResult()
    }

    suspend fun deleteList(id: String) = restDataSource.deleteList(id)
        .onSuccess {
            cacheDataSource.deleteTasksByList(id)
            cacheDataSource.deleteTaskList(id)
        }.toDataResult()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTasksByList(listId: String, isCompleted: Boolean) =
        getListById(listId)
            .flatMapLatest { taskList ->
                if (taskList == null) {
                    return@flatMapLatest flowOf(emptyList())
                }
                cacheDataSource.getTasksByList(
                    listId,
                    taskList.sortType,
                    taskList.sortDirection,
                    isCompleted
                ).map { it.toModel() }
            }

    fun getTaskById(id: String) = cacheDataSource.getTaskById(id)
        .map { it?.toModel() }

    suspend fun createTask(listId: String, task: Task): DataResult<Unit> {
        cacheDataSource.insertTask(task.copy(id = newDirtyId(), listId = listId))
        return refreshCache()
    }

    suspend fun updateTask(listId: String, taskId: String, task: Task): DataResult<Unit> {
        cacheDataSource.updateTask(task)
        return restDataSource.updateTask(listId, taskId, task).toDataResult()
    }

    suspend fun moveTask(
        oldListId: String,
        newListId: String,
        taskId: String,
        lastModified: Instant
    ): DataResult<Unit> {
        cacheDataSource.moveTask(newListId, taskId, lastModified)
        return restDataSource.moveTask(oldListId, newListId, taskId, lastModified).toDataResult()
    }

    suspend fun reorderTask(
        listId: String,
        taskId: String,
        fromIndex: Int,
        toIndex: Int,
        lastModified: Instant
    ): DataResult<Unit> {
        cacheDataSource.reorderTask(listId, taskId, fromIndex, toIndex, lastModified)
        return restDataSource.reorderTask(listId, taskId, fromIndex, toIndex, lastModified)
            .toDataResult()
    }

    suspend fun deleteTask(listId: String, taskId: String) =
        restDataSource.deleteTask(listId, taskId)
            .onSuccess { cacheDataSource.deleteTask(taskId) }.toDataResult()

    suspend fun clearCompletedTasks(listId: String) =
        restDataSource.clearCompletedTasks(listId)
            .onSuccess { refreshCache() }.toDataResult()

    suspend fun linkFCMToken(fcmToken: String) =
        restDataSource.linkFCMToken(fcmToken).toDataResult()

    fun getAppTheme() = preferenceDataSource.getAppTheme()

    suspend fun setAppTheme(theme: Theme) = preferenceDataSource.setAppTheme(theme)

    fun useVibrantColors() = preferenceDataSource.useVibrantColors()

    suspend fun setVibrantColors(useVibrantColors: Boolean) =
        preferenceDataSource.setVibrantColors(useVibrantColors)

    fun getEnabledBoardSections() = preferenceDataSource.getDisabledBoardSections()
        .map { disabledSections ->
            val enabledSections = BoardSection.Type.values().toMutableList()
            enabledSections.removeAll(disabledSections.map { BoardSection.Type.valueOf(it) })
            enabledSections
        }

    suspend fun setEnabledForBoardSection(boardSection: BoardSection.Type, enabled: Boolean) =
        preferenceDataSource.setDisabledForBoardSection(boardSection, !enabled)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getBoardSections() =
        getEnabledBoardSections()
            .flatMapLatest { enabledSections ->
                if (enabledSections.isEmpty()) {
                    return@flatMapLatest flowOf(emptyList())
                }

                val sectionFlows = mutableListOf<Flow<BoardSection>>()

                for (it in enabledSections) {
                    sectionFlows.add(getBoardSection(it))
                }

                combine(sectionFlows) { sections ->
                    sections.toList().filter { it.tasks.isNotEmpty() }
                }
            }

    private fun getBoardSection(type: BoardSection.Type) =
        when (type) {
            BoardSection.Type.OVERDUE -> {
                cacheDataSource.getOverdueTasks().map { BoardSection(type, it.toModel()) }
            }

            BoardSection.Type.STARRED -> {
                cacheDataSource.getStarredTasks().map { BoardSection(type, it.toModel()) }
            }

            BoardSection.Type.UPCOMING -> {
                cacheDataSource.getUpcomingTasks().map { BoardSection(type, it.toModel()) }
            }
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPinnedLists() =
        cacheDataSource.getPinnedTaskLists()
            .flatMapLatest { taskLists ->
                if (taskLists.isEmpty()) {
                    return@flatMapLatest flowOf(emptyList())
                }

                val pinnedLists = mutableListOf<Flow<PinnedList>>()

                for (list in taskLists) {
                    pinnedLists.add(getPinnedList(list.toModel()))
                }

                combine(pinnedLists) { lists ->
                    lists.toList().filter { it.topTasks.isNotEmpty() }
                }
            }

    private fun getPinnedList(taskList: TaskList) =
        combine(
            cacheDataSource.getTasksByList(
                taskList.id,
                taskList.sortType,
                TaskList.SortDirection.ASCENDING,
                isCompleted = false
            ),
            cacheDataSource.getTaskCountForList(taskList.id)
        ) { topTasks, totalTaskCount ->
            PinnedList(
                taskList,
                topTasks.take(NUM_TOP_TASKS).map { it.toModel() },
                totalTaskCount?.toInt() ?: 0
            )
        }

    companion object {
        const val NUM_TOP_TASKS = 3
    }
}