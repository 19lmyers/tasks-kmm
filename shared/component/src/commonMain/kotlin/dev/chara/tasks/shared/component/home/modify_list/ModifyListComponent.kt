package dev.chara.tasks.shared.component.home.modify_list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.github.michaelbull.result.Ok
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.TaskListPrefs
import kotlinx.coroutines.Dispatchers
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

    fun onSave(taskList: TaskList, prefs: TaskListPrefs)
}

class DefaultModifyListComponent(
    componentContext: ComponentContext,
    private val listId: String?,
    private val dismiss: () -> Unit,
) : ModifyListComponent, KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(ModifyListUiState())
    override val state = _state.asStateFlow()

    init {
        coroutineScope.launch {
            if (listId != null) {
                val taskList = repository.getListById(listId).firstOrNull()
                val prefs = repository.getListPrefsById(listId).firstOrNull()
                if (taskList != null) {
                    _state.value =
                        state.value.copy(isLoading = false, selectedList = taskList, prefs = prefs)
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

    override fun onSave(taskList: TaskList, prefs: TaskListPrefs) {
        coroutineScope.launch {
            val result =
                if (taskList.id.isEmpty()) {
                    repository.createList(taskList, prefs)
                } else {
                    repository.updateList(taskList.id, taskList)
                    repository.updateListPrefs(taskList.id, prefs)
                }

            if (result is Ok) {
                withContext(Dispatchers.Main) { dismiss() }
            }
        }
    }
}
