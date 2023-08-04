package dev.chara.tasks.shared.component.settings

import com.arkivanov.decompose.ComponentContext
import dev.chara.tasks.shared.component.util.SnackbarMessage
import dev.chara.tasks.shared.component.util.coroutineScope
import dev.chara.tasks.shared.component.util.emitAsMessage
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.board.BoardSection
import dev.chara.tasks.shared.model.preference.Theme
import dev.chara.tasks.shared.model.preference.ThemeVariant
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface SettingsComponent {
    val state: StateFlow<SettingsUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onUp()

    fun setAppTheme(theme: Theme)

    fun setAppThemeVariant(themeVariant: ThemeVariant)

    fun setEnabledForBoardSection(section: BoardSection.Type, enabled: Boolean)

    fun updateList(taskList: TaskList)
}

class DefaultSettingsComponent(
    componentContext: ComponentContext,
    private val navigateUp: () -> Unit
) : SettingsComponent, KoinComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope(Dispatchers.Default + SupervisorJob())

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(SettingsUiState())
    override val state = _state.asStateFlow()

    private var _messages = MutableSharedFlow<SnackbarMessage>()
    override val messages = _messages.asSharedFlow()

    init {
        coroutineScope.launch {
            combine(
                repository.getAppTheme(),
                repository.getAppThemeVariant(),
                repository.getEnabledBoardSections(),
                repository.getLists()
            ) { theme, themeVariant, enabledSections, taskLists ->
                SettingsUiState(
                    isLoading = false,
                    appTheme = theme,
                    appThemeVariant = themeVariant,
                    enabledBoardSections = enabledSections,
                    taskLists = taskLists
                )
            }.collect {
                _state.value = it
            }
        }
    }

    override fun onUp() = navigateUp()

    override fun setAppTheme(theme: Theme) {
        coroutineScope.launch {
            repository.setAppTheme(theme)
        }
    }

    override fun setAppThemeVariant(themeVariant: ThemeVariant) {
        coroutineScope.launch {
            repository.setAppThemeVariant(themeVariant)
        }
    }

    override fun setEnabledForBoardSection(section: BoardSection.Type, enabled: Boolean) {
        coroutineScope.launch {
            repository.setEnabledForBoardSection(section, enabled)
        }
    }

    override fun updateList(taskList: TaskList) {
        coroutineScope.launch {
            val result = repository.updateList(taskList.id, taskList)
            _messages.emitAsMessage(result)
        }
    }
}