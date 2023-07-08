package dev.chara.tasks.android.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.ToggleableStateKey
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import dev.chara.tasks.android.ui.theme.dynamic.GlanceColorTheme
import dev.chara.tasks.android.worker.CompleteTaskWorker
import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.model.board.BoardSection
import dev.chara.tasks.model.board.PinnedList
import dev.chara.tasks.model.preference.ThemeVariant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TaskListWidget : GlanceAppWidget(), KoinComponent {

    private val repository: Repository by inject()

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val themeVariant = repository.getAppThemeVariant()
                    .collectAsState(initial = ThemeVariant.TONAL_SPOT)

                val allLists = repository.getLists().collectAsState(listOf())

                val state = currentState<Preferences>()
                val selectedListId = state[KEY_SELECTED_LIST_ID]

                if (selectedListId == null) {
                    val boardSections = repository.getBoardSections().collectAsState(listOf())
                    val pinnedLists = repository.getPinnedLists().collectAsState(listOf())

                    Dashboard(
                        boardSections.value,
                        pinnedLists.value,
                        allLists.value,
                        themeVariant.value
                    )
                } else {
                    val list = repository.getListById(selectedListId)
                        .collectAsState(null)

                    if (list.value != null) {
                        val tasks = repository.getTasksByList(selectedListId, false)
                            .collectAsState(listOf())

                        TaskList(list.value!!, tasks.value)
                    }
                }
            }
        }
    }

    companion object {
        private val KEY_SELECTED_LIST_ID = stringPreferencesKey("selected_list_id")
    }
}

@Composable
fun Dashboard(
    boardSections: List<BoardSection>,
    pinnedLists: List<PinnedList>,
    allLists: List<TaskList>,
    themeVariant: ThemeVariant = ThemeVariant.TONAL_SPOT,
) {
    LazyColumn(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .cornerRadius(12.dp)
            .background(GlanceTheme.colors.background)
            .padding(8.dp)
            //.clickable(onClick = actionStartActivity<MainActivity>())
    ) {
        item {
            Header(title = "Dashboard")
        }

        for (boardSection in boardSections) {
            item {
                Subhead(title = boardSection.type.title)
            }

            items(boardSection.tasks) { task ->
                val list = allLists.find { list -> list.id == task.listId }

                GlanceColorTheme(list?.color, themeVariant) {
                    Task(task)
                }
            }
        }

        for (pinnedList in pinnedLists) {
            item {
                Subhead(title = pinnedList.taskList.title)
            }

            items(pinnedList.topTasks) { task ->
                GlanceColorTheme(pinnedList.taskList.color, themeVariant) {
                    Task(task)
                }
            }
        }
    }
}

@Composable
fun TaskList(list: TaskList, tasks: List<Task>) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .cornerRadius(12.dp)
            .background(GlanceTheme.colors.background)
            .padding(8.dp)
            //.clickable(onClick = actionStartActivity<MainActivity>())
    ) {
        Header(title = list.title)

        for (task in tasks) {
            Task(task)
        }
    }
}

@Composable
fun Header(title: String) {
    Text(title, style = TextStyle(color = GlanceTheme.colors.onBackground, fontSize = 24.sp))
}

@Composable
fun Subhead(title: String) {
    Text(title, style = TextStyle(color = GlanceTheme.colors.onBackground, fontSize = 18.sp))
}

@Composable
fun Task(task: Task) {
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(GlanceTheme.colors.surfaceVariant)
            .cornerRadius(24.dp)
            .padding(12.dp)
            //.clickable(onClick = actionStartActivity<MainActivity>())
    ) {
        Column {
            Row {
                CheckBox(
                    checked = task.isCompleted,
                    onCheckedChange = actionRunCallback<TaskCompleteAction>(
                        actionParametersOf(taskIdKey to task.id)
                    )
                )
                Column(
                    modifier = GlanceModifier.fillMaxWidth()
                ) {
                    Text(
                        text = task.label,
                        maxLines = 5,
                        style = TextStyle(color = GlanceTheme.colors.onBackground)
                    )
                    if (!task.details.isNullOrBlank()) {
                        Text(
                            text = task.details!!,
                            maxLines = 2,
                            style = TextStyle(GlanceTheme.colors.onSurface),
                        )
                    }
                }
            }
        }
    }
}

private val taskIdKey = ActionParameters.Key<String>("TaskId")

class TaskCompleteAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val taskId = requireNotNull(parameters[taskIdKey])
        val checked = requireNotNull(parameters[ToggleableStateKey])

        val completeTaskRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CompleteTaskWorker>()
                .setInputData(
                    workDataOf(
                        CompleteTaskWorker.TASK_ID to taskId,
                        CompleteTaskWorker.IS_COMPLETED to checked
                    )
                ).build()

        WorkManager.getInstance(context)
            .enqueue(completeTaskRequest)
    }
}

class TaskListWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = TaskListWidget()
}