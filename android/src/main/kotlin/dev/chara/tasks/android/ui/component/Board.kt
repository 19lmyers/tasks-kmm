package dev.chara.tasks.android.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chara.tasks.android.ui.theme.ColorTheme
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.model.board.BoardSection
import dev.chara.tasks.model.board.PinnedList

private const val CONTENT_TYPE_HEADER = "CONTENT_TYPE_HEADER"
private const val CONTENT_TYPE_TASK = "CONTENT_TYPE_TASK"
private const val CONTENT_TYPE_LIST = "CONTENT_TYPE_LIST"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Dashboard(
    contentPadding: PaddingValues,
    sections: List<BoardSection>,
    pinnedLists: List<PinnedList>,
    allLists: List<TaskList>,
    onTaskClicked: (Task) -> Unit,
    onListClicked: (TaskList) -> Unit,
    onCreateListClicked: () -> Unit,
    onUpdate: (Task) -> Unit
) {
    val state = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        state = state
    ) {
        for (section in sections) {
            stickyHeader(key = section.type, contentType = CONTENT_TYPE_HEADER) {
                SectionHeader(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    title = section.type.title
                )
            }

            items(
                section.tasks,
                key = { "${section.type.name}/${it.id}" },
                contentType = { CONTENT_TYPE_TASK }
            ) { task ->
                val parentList = allLists.first { it.id == task.listId }

                ColorTheme(color = parentList.color) {
                    TaskItem(
                        modifier = Modifier
                            .animateItemPlacement()
                            .padding(horizontal = 16.dp),
                        task = task,
                        parentList = parentList,
                        onListClicked = {
                            onListClicked(it)
                        },
                        onClick = {
                            onTaskClicked(it)
                        },
                        onUpdate = onUpdate
                    )
                }
            }
        }

        for (pinnedList in pinnedLists) {
            stickyHeader(key = pinnedList.taskList.id, contentType = CONTENT_TYPE_HEADER) {
                SectionHeader(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    title = pinnedList.taskList.title
                )
            }

            itemsIndexed(
                pinnedList.topTasks,
                key = { _, task -> "${pinnedList.taskList.id}/${task.id}" },
                contentType = { _, _ -> CONTENT_TYPE_TASK }) { index, task ->

                ColorTheme(color = pinnedList.taskList.color) {
                    TaskItem(
                        modifier = Modifier
                            .animateItemPlacement()
                            .padding(horizontal = 16.dp),
                        task = task,
                        onClick = {
                            onTaskClicked(it)
                        },
                        onUpdate = onUpdate,
                        showIndexNumbers = pinnedList.taskList.showIndexNumbers,
                        indexNumber = index + 1
                    )
                }
            }

            val count = pinnedList.totalTaskCount - pinnedList.topTasks.size

            if (count > 0) {
                item {
                    ViewMore(
                        modifier = Modifier
                            .animateItemPlacement()
                            .padding(horizontal = 16.dp),
                        count = count
                    ) {
                        onListClicked(pinnedList.taskList)
                    }
                }
            }
        }

        stickyHeader(contentType = CONTENT_TYPE_HEADER) {
            SectionHeader(
                modifier = Modifier.padding(horizontal = 8.dp),
                title = "Lists"
            )
        }

        items(
            allLists,
            key = { "lists/${it.id}" },
            contentType = { CONTENT_TYPE_LIST }) { taskList ->
            TaskListItem(
                Modifier
                    .animateItemPlacement()
                    .padding(horizontal = 16.dp),
                taskList,
                onListClicked
            )
        }

        item {
            CreateListItem(
                Modifier
                    .animateItemPlacement()
                    .padding(horizontal = 16.dp),
                onClick = onCreateListClicked
            )

            Spacer(modifier = Modifier.padding(40.dp))
        }
    }
}

@Composable
private fun SectionHeader(modifier: Modifier = Modifier, title: String, isPinned: Boolean = false) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = if (isPinned) 3.dp else 0.dp
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Preview
@Composable
private fun Preview_SectionHeader() {
    SectionHeader(title = "Section")
}

@Composable
private fun ViewMore(
    modifier: Modifier = Modifier,
    count: Int,
    onClick: () -> Unit
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .clip(MaterialTheme.shapes.extraLarge)
        .clickable { onClick() }
        .padding(16.dp)
    ) {
        Text(
            text = "View $count more",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Icon(
            Icons.Filled.OpenInNew,
            contentDescription = "Show list",
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Preview
@Composable
private fun Preview_ViewMore() {
    ViewMore(count = 2) {}
}