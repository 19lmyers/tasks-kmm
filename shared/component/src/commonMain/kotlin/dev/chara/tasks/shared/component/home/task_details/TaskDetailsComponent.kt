package dev.chara.tasks.shared.component.home.task_details

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.github.michaelbull.result.Ok
import dev.chara.tasks.shared.component.util.coroutineScope
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface TaskDetailsComponent {
    val state: StateFlow<TaskDetailsUiState>

    fun onUp()

    fun setShowConfirmExit(showConfirmExit: Boolean)

    fun updateTask(task: Task)

    fun moveTask(oldListId: String, newListId: String, taskId: String)

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

    private val callback =
        BackCallback(priority = 1) { setShowConfirmExit(!state.value.showConfirmExit) }

    init {
        backHandler.register(callback)
    }

    override fun setShowConfirmExit(showConfirmExit: Boolean) {
        _state.value = state.value.copy(showConfirmExit = showConfirmExit)
    }

    override fun updateTask(task: Task) {
        coroutineScope.launch {
            val result = repository.updateTask(task.listId, task.id, task)
            // _messages.emitAsMessage(result)
        }
    }

    override fun moveTask(oldListId: String, newListId: String, taskId: String) {
        coroutineScope.launch {
            val result =
                repository.moveTask(oldListId, newListId, taskId, lastModified = Clock.System.now())
            // _messages.emitAsMessage(result)
        }
    }

    override fun deleteTask(task: Task) {
        coroutineScope.launch {
            val result = repository.deleteTask(task.listId, task.id)
            // _messages.emitAsMessage(result, successMessage = "List deleted")
            if (result is Ok) {
                withContext(Dispatchers.Main) { navigateUp() }
            }
        }
    }
}
