package dev.chara.tasks.shared.component.home

import com.arkivanov.decompose.Child as DecomposeChild
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.children.NavState
import com.arkivanov.decompose.router.children.SimpleChildNavState
import com.arkivanov.decompose.router.children.SimpleNavigation
import com.arkivanov.decompose.router.children.children
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.github.michaelbull.result.Ok
import dev.chara.tasks.shared.component.home.create_task.CreateTaskComponent
import dev.chara.tasks.shared.component.home.create_task.DefaultCreateTaskComponent
import dev.chara.tasks.shared.component.home.dashboard.DashboardComponent
import dev.chara.tasks.shared.component.home.dashboard.DefaultDashboardComponent
import dev.chara.tasks.shared.component.home.join_list.DefaultJoinListComponent
import dev.chara.tasks.shared.component.home.join_list.JoinListComponent
import dev.chara.tasks.shared.component.home.list_details.DefaultListDetailsComponent
import dev.chara.tasks.shared.component.home.list_details.ListDetailsComponent
import dev.chara.tasks.shared.component.home.modify_list.DefaultModifyListComponent
import dev.chara.tasks.shared.component.home.modify_list.ModifyListComponent
import dev.chara.tasks.shared.component.home.share_list.DefaultShareListComponent
import dev.chara.tasks.shared.component.home.share_list.ShareListComponent
import dev.chara.tasks.shared.component.home.task_details.DefaultTaskDetailsComponent
import dev.chara.tasks.shared.component.home.task_details.TaskDetailsComponent
import dev.chara.tasks.shared.component.util.SnackbarMessage
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.domain.Gravatar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface HomeComponent {
    val children: Value<Children>

    val displayedSheet: Value<ChildSlot<*, Sheet>>

    val state: StateFlow<HomeUiState>

    val messages: SharedFlow<SnackbarMessage>

    fun onUp()

    fun onProfile()

    fun onSettings()

    fun getGravatarUri(email: String): String

    fun setDualPane(isDualPane: Boolean)

    fun requestVerifyEmailResend()

    fun clearVerifyEmailNotice()

    fun signOut()

    data class Children(
        val isDualPane: Boolean,
        val main: DecomposeChild.Created<*, Child.Main>,
        val stack: List<DecomposeChild.Created<*, Child.Stack>>,
    )

    sealed class Child {
        sealed class Main : Child() {
            class Dashboard(val component: DashboardComponent) : Main()
        }

        sealed class Stack : Child() {
            class ListDetails(val component: ListDetailsComponent) : Stack()

            class TaskDetails(val component: TaskDetailsComponent) : Stack()
        }
    }

    sealed class Sheet {
        class ModifyList(val component: ModifyListComponent) : Sheet()

        class ShareList(val component: ShareListComponent) : Sheet()

        class JoinList(val component: JoinListComponent) : Sheet()

        class CreateTask(val component: CreateTaskComponent) : Sheet()
    }
}

class DefaultHomeComponent(
    componentContext: ComponentContext,
    defaultListId: String? = null,
    defaultTaskId: String? = null,
    defaultListInviteToken: String? = null,
    showCreateTaskSheet: Boolean = false,
    private val navigateToWelcome: () -> Unit,
    private val navigateToProfile: () -> Unit,
    private val navigateToSettings: () -> Unit,
) : HomeComponent, KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val repository: Repository by inject()

    private var _state = MutableStateFlow(HomeUiState())
    override val state = _state.asStateFlow()

    private var _messages = MutableSharedFlow<SnackbarMessage>()
    override val messages = _messages.asSharedFlow()

    init {
        coroutineScope.launch {
            repository.isUserAuthenticated().collect { isAuthenticated ->
                if (!isAuthenticated) {
                    withContext(Dispatchers.Main) { navigateToWelcome() }
                }
            }
        }

        coroutineScope.launch {
            repository.getUserProfile().collect { profile ->
                _state.value = HomeUiState(profile = profile)
            }
        }
    }

    private val navigation = SimpleNavigation<(NavigationState) -> NavigationState>()

    private fun SimpleNavigation<(NavigationState) -> NavigationState>.push(config: Config.Stack) {
        navigate { state ->
            if (state.stack.contains(config)) {
                state.copy(stack = state.stack - config + config)
            } else {
                state.copy(stack = state.stack + config)
            }
        }
        callback.isEnabled = true
    }

    private fun SimpleNavigation<(NavigationState) -> NavigationState>.pop() {
        navigate { state ->
            val stack = state.stack.dropLast(1)

            callback.isEnabled = stack.isNotEmpty()

            state.copy(stack = stack)
        }
    }

    override val children: Value<HomeComponent.Children> =
        children(
            source = navigation,
            key = "children",
            initialState = {
                if (defaultTaskId != null) {
                    NavigationState(stack = listOf(Config.Stack.TaskDetails(defaultTaskId)))
                } else if (defaultListId != null) {
                    NavigationState(stack = listOf(Config.Stack.ListDetails(defaultListId)))
                } else {
                    NavigationState()
                }
            },
            navTransformer = { navState, event -> event(navState) },
            stateMapper = { navState, children ->
                @Suppress("UNCHECKED_CAST")
                (HomeComponent.Children(
                    isDualPane = navState.isDualPane,
                    main =
                        children.first { it.instance is HomeComponent.Child.Main }
                            as DecomposeChild.Created<*, HomeComponent.Child.Main>,
                    stack =
                        children.filter { it.instance is HomeComponent.Child.Stack }
                            as List<DecomposeChild.Created<*, HomeComponent.Child.Stack>>,
                ))
            },
            childFactory = ::child,
        )

    private fun child(config: Config, componentContext: ComponentContext) =
        when (config) {
            is Config.Main.Dashboard ->
                HomeComponent.Child.Main.Dashboard(
                    DefaultDashboardComponent(
                        componentContext,
                        navigateToCreateList = {
                            sheetNavigation.activate(SheetConfig.ModifyList(listId = null))
                        },
                        navigateToCreateTask = {
                            sheetNavigation.activate(SheetConfig.CreateTask(listId = null))
                        },
                        navigateToListDetails = { taskList ->
                            navigation.push(Config.Stack.ListDetails(taskList.id))
                        },
                        navigateToTaskDetails = { task ->
                            navigation.push(Config.Stack.TaskDetails(task.id))
                        }
                    )
                )
            is Config.Stack.ListDetails ->
                HomeComponent.Child.Stack.ListDetails(
                    DefaultListDetailsComponent(
                        componentContext,
                        listId = config.id,
                        navigateUp = { navigation.pop() },
                        navigateToModifyList = { listId ->
                            sheetNavigation.activate(SheetConfig.ModifyList(listId = listId))
                        },
                        navigateToShareList = { listId ->
                            sheetNavigation.activate(SheetConfig.ShareList(listId = listId))
                        },
                        navigateToTaskDetails = { taskId ->
                            navigation.push(Config.Stack.TaskDetails(taskId))
                        },
                        navigateToCreateTask = {
                            sheetNavigation.activate(SheetConfig.CreateTask(config.id))
                        }
                    )
                )
            is Config.Stack.TaskDetails ->
                HomeComponent.Child.Stack.TaskDetails(
                    DefaultTaskDetailsComponent(
                        componentContext,
                        taskId = config.id,
                        navigateUp = { navigation.pop() }
                    )
                )
        }

    private val sheetNavigation = SlotNavigation<SheetConfig>()

    private val sheetSlot =
        childSlot(
            source = sheetNavigation,
            initialConfiguration = {
                if (showCreateTaskSheet) {
                    SheetConfig.CreateTask(listId = null)
                } else if (defaultListInviteToken != null) {
                    SheetConfig.JoinList(defaultListInviteToken)
                } else {
                    null
                }
            },
            childFactory = ::sheet,
            handleBackButton = true
        )

    override val displayedSheet: Value<ChildSlot<*, HomeComponent.Sheet>> = sheetSlot

    private fun sheet(config: SheetConfig, componentContext: ComponentContext) =
        when (config) {
            is SheetConfig.ModifyList ->
                HomeComponent.Sheet.ModifyList(
                    DefaultModifyListComponent(
                        componentContext,
                        listId = config.listId,
                        dismiss = { sheetNavigation.dismiss() }
                    )
                )
            is SheetConfig.ShareList -> {
                HomeComponent.Sheet.ShareList(
                    DefaultShareListComponent(
                        componentContext,
                        listId = config.listId,
                        dismiss = { sheetNavigation.dismiss() }
                    )
                )
            }
            is SheetConfig.JoinList -> {
                HomeComponent.Sheet.JoinList(
                    DefaultJoinListComponent(
                        componentContext,
                        inviteToken = config.inviteToken,
                        dismiss = { sheetNavigation.dismiss() }
                    )
                )
            }
            is SheetConfig.CreateTask ->
                HomeComponent.Sheet.CreateTask(
                    DefaultCreateTaskComponent(
                        componentContext,
                        listId = config.listId,
                        dismiss = { sheetNavigation.dismiss() }
                    )
                )
        }

    override fun onUp() {
        navigation.pop()
    }

    private val callback = BackCallback { onUp() }

    init {
        backHandler.register(callback)
    }

    override fun onProfile() = navigateToProfile()

    override fun onSettings() = navigateToSettings()

    override fun getGravatarUri(email: String) = Gravatar.getUri(email)

    override fun setDualPane(isDualPane: Boolean) =
        navigation.navigate { it.copy(isDualPane = isDualPane) }

    override fun requestVerifyEmailResend() {
        coroutineScope.launch {
            val result = repository.requestVerifyEmailResend()
            _state.value = state.value.copy(verifyEmailSent = result is Ok)
        }
    }

    override fun clearVerifyEmailNotice() {
        _state.value = state.value.copy(verifyEmailSent = false)
    }

    override fun signOut() {
        coroutineScope.launch { repository.logout() }
    }

    private sealed interface Config : Parcelable {
        sealed interface Main : Config {
            @Parcelize data object Dashboard : Main
        }

        sealed interface Stack : Config {

            @Parcelize data class ListDetails(val id: String) : Stack

            @Parcelize data class TaskDetails(val id: String) : Stack
        }
    }

    @Parcelize
    private data class NavigationState(
        val isDualPane: Boolean = false,
        val stack: List<Config> = listOf()
    ) : NavState<Config>, Parcelable {
        override val children: List<ChildNavState<Config>>
            get() =
                listOf(
                    SimpleChildNavState(
                        Config.Main.Dashboard,
                        if (isDualPane || stack.isEmpty()) ChildNavState.Status.ACTIVE
                        else ChildNavState.Status.INACTIVE
                    )
                ) +
                    stack.mapIndexed { index, config ->
                        SimpleChildNavState(
                            config,
                            if (stack.lastIndex == index) ChildNavState.Status.ACTIVE
                            else ChildNavState.Status.INACTIVE
                        )
                    }
    }

    private sealed interface SheetConfig : Parcelable {
        @Parcelize data class ModifyList(val listId: String?) : SheetConfig

        @Parcelize data class ShareList(val listId: String) : SheetConfig

        @Parcelize data class JoinList(val inviteToken: String) : SheetConfig

        @Parcelize data class CreateTask(val listId: String?) : SheetConfig
    }
}
