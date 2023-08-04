package dev.chara.tasks.shared.component.home.dashboard

import com.arkivanov.decompose.ComponentContext
import dev.chara.tasks.shared.component.util.coroutineScope
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface DashboardComponent {
    val state: StateFlow<DashboardUiState>

    fun onRefresh()

    fun onListClicked(taskList: TaskList)

    fun onTaskClicked(task: Task)

    fun onCreateList()

    fun onCreateTask()

    fun updateTask(task: Task)
}

class DefaultDashboardComponent(
    componentContext: ComponentContext,
    private val navigateToCreateList: () -> Unit,
    private val navigateToCreateTask: () -> Unit,
    private val navigateToListDetails: (TaskList) -> Unit,
    private val navigateToTaskDetails: (Task) -> Unit,
) : DashboardComponent, KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope(Dispatchers.Default + SupervisorJob())

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(DashboardUiState())
    override val state = _state.asStateFlow()

    init {
        coroutineScope.launch {
            combine(
                repository.getBoardSections(),
                repository.getBoardLists(),
                repository.getLists(),
            ) { boardSections, pinnedLists, allLists ->
                DashboardUiState(
                    isLoading = false,
                    isRefreshing = false,
                    boardSections = boardSections,
                    boardLists = pinnedLists,
                    allLists = allLists
                )
            }.collect {
                _state.value = it
            }
        }
    }

    override fun onRefresh() {
        coroutineScope.launch {
            _state.value = state.value.copy(isRefreshing = true)

            val result = repository.refresh()
            //_messages.emitAsMessage(result) TODO fix snackbar
        }
    }

    override fun onCreateList() = navigateToCreateList()

    override fun onCreateTask() = navigateToCreateTask()
    override fun onListClicked(taskList: TaskList) = navigateToListDetails(taskList)

    override fun onTaskClicked(task: Task) = navigateToTaskDetails(task)

    override fun updateTask(task: Task) {
        coroutineScope.launch {
            repository.updateTask(task.listId, task.id, task)
            //_messages.emitAsMessage(result, successMessage = "Task updated") TODO fix snackbar
        }
    }
}