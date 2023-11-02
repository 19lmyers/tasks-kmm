package dev.chara.tasks.android.ui.widget

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
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
import androidx.glance.appwidget.lazy.LazyListScope
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ColumnScope
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
import dev.chara.tasks.android.R
import dev.chara.tasks.android.model.drawable
import dev.chara.tasks.android.ui.widget.theme.GlanceAppTheme
import dev.chara.tasks.android.ui.widget.theme.GlanceColorTheme
import dev.chara.tasks.android.ui.widget.theme.GlanceDynamicTheme
import dev.chara.tasks.android.ui.widget.theme.surfaceContainer
import dev.chara.tasks.android.ui.widget.theme.surfaceContainerHigh
import dev.chara.tasks.android.worker.CompleteTaskWorker
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.preference.ThemeVariant
import dev.chara.tasks.shared.ui.model.color
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TaskListWidget : GlanceAppWidget(), KoinComponent {

    private val repository: Repository by inject()

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val themeVariant = repository.getAppThemeVariant()

        val boardSections = repository.getBoardSections()
        val boardLists = repository.getBoardLists()

        val allLists = repository.getLists()

        provideContent {
            val themeVariantState = themeVariant.collectAsState(initial = ThemeVariant.TONAL_SPOT)

            GlanceAppTheme(variant = themeVariantState.value) {
                val state = currentState<Preferences>()
                val showLists = state[KEY_SHOW_LISTS]

                if (true) { // todo implement condition
                    val boardListsState = boardLists.collectAsState(listOf())

                    Dashboard {
                        item { Header(title = "Lists") }

                        items(boardListsState.value) { boardList ->
                            ListCard(
                                title = boardList.taskList.title,
                                description = boardList.taskList.description,
                                tintColor = boardList.taskList.color,
                                icon =
                                    boardList.taskList.icon?.drawable ?: R.drawable.checklist_48px
                            ) {
                                for ((index, task) in boardList.topTasks.withIndex()) {
                                    Task(task = task)
                                }

                                val currentLeft =
                                    boardList.currentTaskCount - boardList.topTasks.size

                                if (boardList.currentTaskCount - boardList.topTasks.size > 0) {
                                    ViewMore("View $currentLeft more")
                                } else if (boardList.completedTaskCount > 0) {
                                    ViewMore("Completed (${boardList.completedTaskCount})")
                                } else if (boardList.topTasks.isEmpty()) {
                                    Text(
                                        "All tasks complete!",
                                        modifier = GlanceModifier.fillMaxWidth().padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    val boardSectionsState = boardSections.collectAsState(listOf())

                    val allListsState = allLists.collectAsState(listOf())

                    Dashboard {
                        item { Header(title = "Dashboard") }

                        items(boardSectionsState.value) { boardSection ->
                            BoardCard(
                                title = boardSection.type.title,
                                tintColor = boardSection.type.color,
                                icon = boardSection.type.drawable
                            ) {
                                for (task in boardSection.tasks) {
                                    val parentList =
                                        allListsState.value.first { it.id == task.listId }

                                    GlanceColorTheme(color = parentList.color) { Task(task = task) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val KEY_SHOW_LISTS = booleanPreferencesKey("show_lists")
    }
}

@Composable
fun Dashboard(content: LazyListScope.() -> Unit) {
    LazyColumn(
        modifier =
            GlanceModifier.fillMaxSize()
                .appWidgetBackground()
                .cornerRadius(28.dp)
                .background(GlanceTheme.colors.background)
                .padding(8.dp)
        // .clickable(onClick = actionStartActivity<MainActivity>())
    ) {
        content()
    }
}

@Composable
fun Header(title: String) {
    Text(title, style = TextStyle(color = GlanceTheme.colors.onBackground, fontSize = 24.sp))
}

@Composable
private fun BoardCard(
    title: String,
    description: String? = null,
    @DrawableRes icon: Int,
    tintColor: Color,
    content: @Composable ColumnScope.() -> Unit,
) {
    GlanceDynamicTheme(seed = tintColor) {
        Box(
            modifier =
                GlanceModifier.cornerRadius(28.dp)
                    .background(GlanceTheme.colors.surfaceContainer)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
        ) {
            CardContent(title = title, description = description, icon = icon, content = content)
        }
    }
}

@Composable
private fun ListCard(
    title: String,
    description: String? = null,
    @DrawableRes icon: Int,
    tintColor: TaskList.Color?,
    content: @Composable ColumnScope.() -> Unit,
) {
    GlanceColorTheme(color = tintColor) {
        Box(
            modifier =
                GlanceModifier.cornerRadius(28.dp)
                    .background(GlanceTheme.colors.surfaceContainer)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
        ) {
            CardContent(title = title, description = description, icon = icon, content = content)
        }
    }
}

@Composable
private fun CardContent(
    title: String,
    @DrawableRes icon: Int,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = GlanceModifier.padding(horizontal = 8.dp, vertical = 12.dp)) {
        Row(modifier = GlanceModifier.padding(horizontal = 16.dp).fillMaxWidth()) {
            Image(
                modifier = GlanceModifier.padding(8.dp),
                provider = ImageProvider(icon),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.primary),
                contentDescription = null
            )
            Text(
                modifier = GlanceModifier.padding(8.dp),
                text = title,
                style = TextStyle(fontSize = 16.sp)
            )
        }
        if (description != null) {
            Text(
                modifier =
                    GlanceModifier.padding(horizontal = 8.dp).padding(start = 16.dp, bottom = 8.dp),
                text = description,
                style = TextStyle(fontSize = 16.sp)
            )
        }
        content()
    }
}

@Composable
fun Task(task: Task) {
    Box(
        modifier =
            GlanceModifier.fillMaxWidth()
                .background(GlanceTheme.colors.surfaceContainerHigh)
                .cornerRadius(24.dp)
                .padding(12.dp)
        // .clickable(onClick = actionStartActivity<MainActivity>())
    ) {
        Column {
            Row {
                CheckBox(
                    checked = task.isCompleted,
                    onCheckedChange =
                        actionRunCallback<TaskCompleteAction>(
                            actionParametersOf(taskIdKey to task.id)
                        )
                )
                Column(modifier = GlanceModifier.fillMaxWidth()) {
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

@Composable
private fun ViewMore(text: String) {
    Box(
        modifier =
            GlanceModifier.fillMaxWidth()
                .cornerRadius(28.dp)
                .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = 16.sp),
        )
        Image(
            provider = ImageProvider(R.drawable.open_in_new_48px),
            contentDescription = "Show list",
        )
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
                )
                .build()

        WorkManager.getInstance(context).enqueue(completeTaskRequest)
    }
}

class TaskListWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = TaskListWidget()
}
