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
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
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
import dev.chara.tasks.data.Repository
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.model.board.BoardSection
import dev.chara.tasks.model.board.PinnedList
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TaskListWidget : GlanceAppWidget(), KoinComponent {

    private val repository: Repository by inject()

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val lists = repository.getLists().collectAsState(listOf())

                val state = currentState<Preferences>()
                val selectedListId = state[KEY_SELECTED_LIST_ID]

                if (selectedListId == null) {
                    val boardSections = repository.getBoardSections().collectAsState(listOf())
                    val pinnedLists = repository.getPinnedLists().collectAsState(listOf())

                    Dashboard(boardSections.value, pinnedLists.value)
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
fun Dashboard(boardSections: List<BoardSection>, pinnedLists: List<PinnedList>) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(12.dp)
            .background(GlanceTheme.colors.background)
            .padding(8.dp)
    ) {
        Header(title = "Dashboard")

        for (boardSection in boardSections) {
            Subhead(title = boardSection.type.title)

            for (task in boardSection.tasks) {
                Task(task)
            }
        }

        for (pinnedList in pinnedLists) {
            Subhead(title = pinnedList.taskList.title)

            for (task in pinnedList.topTasks) {
                Task(task)
            }
        }
    }
}

@Composable
fun TaskList(list: TaskList, tasks: List<Task>) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(12.dp)
            .background(GlanceTheme.colors.background)
            .padding(8.dp)
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
    Box(modifier = GlanceModifier.fillMaxWidth()) {
        Column {
            Row {
                CheckBox(
                    checked = task.isCompleted,
                    onCheckedChange = {
                        // TODO
                    }
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

class TaskListWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TaskListWidget()
}