package dev.chara.tasks.shared.component.home.create_task

import com.arkivanov.decompose.ComponentContext
import com.github.michaelbull.result.Ok
import dev.chara.tasks.shared.component.util.coroutineScope
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface CreateTaskComponent {
    val state: StateFlow<CreateTaskUiState>

    val defaultListId: String?

    fun onDismiss()

    fun onSave(task: Task)
}

class DefaultCreateTaskComponent(
    componentContext: ComponentContext,
    listId: String?,
    private val dismiss: () -> Unit
) : CreateTaskComponent, KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope(Dispatchers.Default + SupervisorJob())

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(CreateTaskUiState())
    override val state = _state.asStateFlow()

    init {
        coroutineScope.launch {
            repository.getLists().collect { taskLists ->
                _state.value = CreateTaskUiState(isLoading = false, allLists = taskLists)
            }
        }
    }

    override val defaultListId = listId

    override fun onDismiss() {
        if (!state.value.isLoading) {
            dismiss()
        }
    }

    override fun onSave(task: Task) {
        coroutineScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val result = repository.createTask(task.listId, task)

            if (result is Ok) {
                withContext(Dispatchers.Main) { dismiss() }
            }

            _state.value = state.value.copy(isLoading = false)
        }
    }
}
