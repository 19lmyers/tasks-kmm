package dev.chara.tasks.shared.component.home.task_details

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.github.michaelbull.result.Ok
import dev.chara.tasks.shared.component.util.SnackbarMessage
import dev.chara.tasks.shared.component.util.coroutineScope
import dev.chara.tasks.shared.component.util.emitAsMessage
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

interface TaskDetailsComponent : BackHandlerOwner {
    val state: StateFlow<TaskDetailsUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onUp()

    fun updateTask(task: Task)

    fun moveTask(oldListId: String, newListId: String, task: Task)

    fun deleteTask(task: Task)
}

class DefaultTaskDetailsComponent(
    componentContext: ComponentContext,
    taskId: String,
    private val navigateUp: () -> Unit
) : TaskDetailsComponent, KoinComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope(Dispatchers.Default + SupervisorJob())

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(TaskDetailsUiState())
    override val state = _state.asStateFlow()

    private var _messages = MutableSharedFlow<SnackbarMessage>()
    override val messages = _messages.asSharedFlow()

    init {
        coroutineScope.launch {
            combine(
                    repository.getTaskById(taskId),
                    repository.getLists(),
                ) { task, taskLists ->
                    TaskDetailsUiState(
                        isLoading = false,
                        selectedTask = task,
                        allLists = taskLists,
                    )
                }
                .collect {
                    if (it.selectedTask == null) {
                        withContext(Dispatchers.Main) { navigateUp() }
                    } else {
                        _state.value = it
                    }
                }
        }
    }

    override fun onUp() = navigateUp()

    override fun updateTask(task: Task) {
        coroutineScope.launch {
            val result = repository.updateTask(task.listId, task.id, task)
            _messages.emitAsMessage(result)
        }
    }

    override fun moveTask(oldListId: String, newListId: String, task: Task) {
        coroutineScope.launch {
            // Always save the user's changes before moving a task, otherwise they will be lost
            val updateResult = repository.updateTask(oldListId, task.id, task)
            _messages.emitAsMessage(updateResult)

            val moveResult =
                repository.moveTask(
                    oldListId,
                    newListId,
                    task.id,
                    lastModified = Clock.System.now()
                )
            _messages.emitAsMessage(moveResult)
        }
    }

    override fun deleteTask(task: Task) {
        coroutineScope.launch {
            val result = repository.deleteTask(task.listId, task.id)
            _messages.emitAsMessage(result, successMessage = "List deleted")
            if (result is Ok) {
                withContext(Dispatchers.Main) { navigateUp() }
            }
        }
    }
}
