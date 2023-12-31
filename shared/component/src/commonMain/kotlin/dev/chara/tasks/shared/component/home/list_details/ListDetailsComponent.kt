package dev.chara.tasks.shared.component.home.list_details

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.github.michaelbull.result.Ok
import dev.chara.tasks.shared.component.util.SnackbarMessage
import dev.chara.tasks.shared.component.util.emitAsMessage
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.TaskListPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ListDetailsComponent {
    val state: StateFlow<ListDetailsUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onUp()

    fun onRefresh()

    fun onCreateTask()

    fun onEditClicked(listId: String)

    fun onShareClicked(listId: String)

    fun onTaskClicked(taskId: String)

    fun updateList(taskList: TaskList)

    fun updatePrefs(prefs: TaskListPrefs)

    fun deleteList(listId: String)

    fun leaveList(listId: String)

    fun updateTask(task: Task)

    fun reorderTask(listId: String, taskId: String, fromIndex: Int, toIndex: Int)

    fun clearCompletedTasks(listId: String)
}

class DefaultListDetailsComponent(
    componentContext: ComponentContext,
    listId: String,
    private val navigateUp: () -> Unit,
    private val navigateToModifyList: (String) -> Unit,
    private val navigateToShareList: (String) -> Unit,
    private val navigateToTaskDetails: (String) -> Unit,
    private val navigateToCreateTask: () -> Unit,
) : ListDetailsComponent, KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(ListDetailsUiState())
    override val state = _state.asStateFlow()

    private var _messages = MutableSharedFlow<SnackbarMessage>()
    override val messages = _messages.asSharedFlow()

    init {
        coroutineScope.launch {
            combine(
                    repository.getUserProfile(),
                    repository.getListById(listId),
                    repository.getListPrefsById(listId),
                    repository.getTasksByList(listId, false),
                    repository.getTasksByList(listId, true)
                ) { profile, list, prefs, currentTasks, completedTasks ->
                    ListDetailsUiState(
                        profile = profile,
                        isLoading = false,
                        isRefreshing = false,
                        selectedList = list,
                        prefs = prefs,
                        currentTasks = currentTasks,
                        completedTasks = completedTasks
                    )
                }
                .collect {
                    if (it.selectedList == null) {
                        withContext(Dispatchers.Main) { navigateUp() }
                    } else {
                        _state.value = it
                    }
                }
        }
    }

    override fun onUp() = navigateUp()

    override fun onRefresh() {
        coroutineScope.launch {
            _state.value = state.value.copy(isRefreshing = true)

            val result = repository.refresh()
            _messages.emitAsMessage(result)
        }
    }

    override fun onCreateTask() = navigateToCreateTask()

    override fun onEditClicked(listId: String) = navigateToModifyList(listId)

    override fun onShareClicked(listId: String) = navigateToShareList(listId)

    override fun onTaskClicked(taskId: String) = navigateToTaskDetails(taskId)

    override fun updateList(taskList: TaskList) {
        coroutineScope.launch {
            val result = repository.updateList(taskList.id, taskList)
            _messages.emitAsMessage(result)
        }
    }

    override fun updatePrefs(prefs: TaskListPrefs) {
        coroutineScope.launch {
            val result = repository.updateListPrefs(prefs.listId, prefs)
            _messages.emitAsMessage(result)
        }
    }

    override fun deleteList(listId: String) {
        coroutineScope.launch {
            val result = repository.deleteList(listId)
            _messages.emitAsMessage(result, successMessage = "List deleted")
            if (result is Ok) {
                withContext(Dispatchers.Main) { navigateUp() }
            }
        }
    }

    override fun leaveList(listId: String) {
        coroutineScope.launch {
            val result = repository.leaveList(listId)
            _messages.emitAsMessage(result, successMessage = "List left")
            if (result is Ok) {
                withContext(Dispatchers.Main) { navigateUp() }
            }
        }
    }

    override fun updateTask(task: Task) {
        coroutineScope.launch {
            val result = repository.updateTask(task.listId, task.id, task)
            _messages.emitAsMessage(result)
        }
    }

    override fun reorderTask(listId: String, taskId: String, fromIndex: Int, toIndex: Int) {
        coroutineScope.launch {
            val result =
                repository.reorderTask(
                    listId,
                    taskId,
                    fromIndex,
                    toIndex,
                    lastModified = Clock.System.now()
                )
            _messages.emitAsMessage(result)
        }
    }

    override fun clearCompletedTasks(listId: String) {
        coroutineScope.launch {
            val result = repository.clearCompletedTasks(listId)
            _messages.emitAsMessage(result)
        }
    }
}
