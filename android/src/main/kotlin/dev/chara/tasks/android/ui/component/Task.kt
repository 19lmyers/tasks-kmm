package dev.chara.tasks.android.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.chara.tasks.android.ui.theme.ColorTheme
import dev.chara.tasks.android.ui.theme.LocalDarkTheme
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import kotlinx.datetime.Clock
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

private const val KEY_DIVIDER = "KEY_DIVIDER"

private const val CONTENT_TYPE_TASK = "CONTENT_TYPE_TASK"
private const val CONTENT_TYPE_DIVIDER = "CONTENT_TYPE_DIVIDER"

@Suppress("UnnecessaryComposedModifier")
fun Modifier.reorderable(
    state: ReorderableLazyListState,
    enabled: Boolean
) = composed(inspectorInfo = debugInspectorInfo {
    name = "reorderable"
    value = enabled
}) {
    if (enabled) {
        reorderable(state)
    } else {
        this
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tasks(
    currentTasks: List<Task>,
    completedTasks: List<Task>,
    showIndexNumbers: Boolean,
    onClick: (Task) -> Unit,
    onUpdate: (Task) -> Unit,
    allowReorder: Boolean,
    onReorder: (String, Int, Int) -> Unit
) {
    var reorderableTasks = remember { currentTasks.toMutableStateList() }

    SideEffect {
        if (reorderableTasks != currentTasks) {
            reorderableTasks.apply {
                clear()
                addAll(currentTasks)
            }
        }
    }

    // TODO store in DB?
    var showCompletedTasks by rememberSaveable { mutableStateOf(false) }

    val reorderableState = rememberReorderableLazyListState(
        onMove = { from, to ->
            reorderableTasks = reorderableTasks.apply {
                add(to.index, removeAt(from.index))
            }
            onReorder(from.key as String, from.index, to.index)
        },
        canDragOver = { from, _ ->
            from.index < reorderableTasks.size
        }
    )

    LazyColumn(
        state = reorderableState.listState,
        modifier = Modifier
            .fillMaxSize()
            .reorderable(reorderableState, allowReorder),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
    ) {
        itemsIndexed(
            reorderableTasks,
            key = { _, task -> task.id },
            contentType = { _, _ -> CONTENT_TYPE_TASK }
        ) { index, task ->
            ReorderableItem(
                reorderableState = reorderableState,
                key = task.id,
                index = index
            ) { isDragging ->
                TaskItem(
                    modifier = Modifier
                        .animateItemPlacement()
                        .detectReorderAfterLongPress(reorderableState)
                        .zIndex(zIndex = if (isDragging) 1f else 0f),
                    task = task,
                    onClick = { onClick(it) },
                    onUpdate = { onUpdate(it) },
                    showIndexNumbers = showIndexNumbers,
                    indexNumber = index + 1,
                    isDragging = isDragging
                )
            }
        }

        if (completedTasks.isNotEmpty()) {
            item(key = KEY_DIVIDER, contentType = CONTENT_TYPE_DIVIDER) {
                CompletedTasksDivider(
                    modifier = Modifier.animateItemPlacement(),
                    expanded = showCompletedTasks,
                    setExpanded = { showCompletedTasks = it },
                    count = completedTasks.size
                )
            }

            if (showCompletedTasks) {
                items(
                    completedTasks,
                    key = { it.id },
                    contentType = { CONTENT_TYPE_TASK }
                ) { task ->
                    TaskItem(
                        modifier = Modifier.animateItemPlacement(),
                        task = task,
                        onClick = { onClick(it) },
                        onUpdate = { onUpdate(it) },
                        showIndexNumbers = false,
                        isDragging = false
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
    onClick: (Task) -> Unit,
    onUpdate: (Task) -> Unit,
    parentList: TaskList? = null,
    onListClicked: (TaskList) -> Unit = {},
    showIndexNumbers: Boolean = false,
    indexNumber: Int? = null,
    isDragging: Boolean = false
) {
    val shadowElevation = animateDpAsState(if (isDragging) 8.dp else 0.dp)

    Row(modifier = modifier) {
        if (showIndexNumbers) {
            Text(
                text = indexNumber.toString(),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 8.dp)
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            onClick = { onClick(task) },
            shadowElevation = shadowElevation.value,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Task(
                modifier = Modifier.padding(4.dp),
                task = task,
                parentList = parentList,
                onClick = onClick,
                onListClicked = onListClicked,
                onUpdate = onUpdate
            )
        }
    }
}

@Preview
@Composable
private fun Preview_TaskItem() {
    ColorTheme(color = TaskList.Color.GREEN) {
        TaskItem(
            task = Task(
                id = "1",
                listId = "1",
                label = "Take out trash",
                details = "Now!!!",
            ),
            parentList = TaskList(
                id = "1",
                title = "List",
                color = TaskList.Color.GREEN
            ),
            onClick = {},
            onUpdate = {}
        )
    }
}

@Preview
@Composable
private fun Preview_TaskItem_Dark() {
    CompositionLocalProvider(LocalDarkTheme provides true) {
        ColorTheme(color = TaskList.Color.GREEN) {
            TaskItem(
                task = Task(
                    id = "1",
                    listId = "1",
                    label = "Take out trash",
                    details = "Now!!!",
                ),
                parentList = TaskList(
                    id = "1",
                    title = "List",
                    color = TaskList.Color.GREEN
                ),
                onClick = {},
                onUpdate = {}
            )
        }
    }
}

@Composable
private fun Task(
    modifier: Modifier = Modifier,
    task: Task,
    onClick: (Task) -> Unit,
    onUpdate: (Task) -> Unit,
    parentList: TaskList? = null,
    onListClicked: (TaskList) -> Unit = {},
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Column {
            Row {
                Checkbox(
                    modifier = Modifier.align(Alignment.Top),
                    checked = task.isCompleted,
                    onCheckedChange = { isCompleted ->
                        onUpdate(
                            task.copy(
                                isCompleted = isCompleted,
                                lastModified = Clock.System.now()
                            )
                        )
                    }
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth(0.85f)
                ) {
                    Text(
                        text = task.label,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (!task.details.isNullOrBlank()) {
                        Text(
                            text = task.details!!,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            TaskChips(
                task = task,
                parentList = parentList,
                onListClicked = onListClicked,
                onTaskClicked = onClick
            )
        }
        IconToggleButton(
            modifier = Modifier
                .align(Alignment.TopEnd),
            checked = task.isStarred,
            onCheckedChange = { isStarred ->
                onUpdate(task.copy(isStarred = isStarred, lastModified = Clock.System.now()))
            }
        ) {
            if (task.isStarred) {
                Icon(Icons.Filled.Star, contentDescription = "Unstar task")
            } else {
                Icon(Icons.Filled.StarOutline, contentDescription = "Star task")
            }
        }
    }
}

@Composable
private fun TaskChips(
    task: Task,
    parentList: TaskList?,
    onListClicked: (TaskList) -> Unit,
    onTaskClicked: (Task) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        if (parentList != null) {
            ListChip(list = parentList, onClick = onListClicked)
        }
        if (task.reminderDate != null) {
            ReminderChip(
                task.reminderDate,
                selectable = false,
                onClick = { onTaskClicked(task) })
        }
        if (task.dueDate != null) {
            DueDateChip(task.dueDate, selectable = false, onClick = { onTaskClicked(task) })
        }
    }
}

@Composable
private fun CompletedTasksDivider(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    count: Int
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .clip(MaterialTheme.shapes.extraLarge)
        .clickable { setExpanded(!expanded) }
        .padding(16.dp)
    ) {
        Text(
            text = "Completed ($count)",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.CenterStart)
        )
        if (expanded) {
            Icon(
                Icons.Filled.ExpandLess,
                contentDescription = "Hide completed tasks",
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        } else {
            Icon(
                Icons.Filled.ExpandMore,
                contentDescription = "Show completed tasks",
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Preview
@Composable
private fun Preview_CompletedTasksDivider() {
    CompletedTasksDivider(expanded = false, setExpanded = {}, count = 4)
}