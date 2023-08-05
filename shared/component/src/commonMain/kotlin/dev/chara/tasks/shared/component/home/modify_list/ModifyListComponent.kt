package dev.chara.tasks.shared.component.home.modify_list

import com.arkivanov.decompose.ComponentContext
import com.github.michaelbull.result.Ok
import dev.chara.tasks.shared.component.util.coroutineScope
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.model.TaskList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ModifyListComponent {
    val state: StateFlow<ModifyListUiState>

    fun onDismiss()

    fun onSave(taskList: TaskList)
}

class DefaultModifyListComponent(
    componentContext: ComponentContext,
    private val listId: String?,
    private val dismiss: () -> Unit,
) : ModifyListComponent, KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope(Dispatchers.Default + SupervisorJob())

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(ModifyListUiState())
    override val state = _state.asStateFlow()

    init {
        coroutineScope.launch {
            if (listId != null) {
                val taskList = repository.getListById(listId).firstOrNull()
                if (taskList != null) {
                    _state.value = state.value.copy(isLoading = false, selectedList = taskList)
                }
            } else {
                _state.value = state.value.copy(isLoading = false)
            }
        }
    }

    override fun onDismiss() {
        if (!state.value.isLoading) {
            dismiss()
        }
    }

    override fun onSave(taskList: TaskList) {
        coroutineScope.launch {
            val result =
                if (taskList.id.isEmpty()) {
                    repository.createList(taskList)
                } else {
                    repository.updateList(taskList.id, taskList)
                }

            if (result is Ok) {
                withContext(Dispatchers.Main) { dismiss() }
            }
        }
    }
}
