package dev.chara.tasks.android.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chara.tasks.android.model.vector
import dev.chara.tasks.android.ui.theme.ColorTheme
import dev.chara.tasks.android.ui.theme.themedContainer
import dev.chara.tasks.model.TaskList

private const val CONTENT_TYPE_LIST = "CONTENT_TYPE_LIST"
private const val CONTENT_TYPE_CREATE = "CONTENT_TYPE_CREATE"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskLists(
    modifier: Modifier = Modifier,
    taskLists: List<TaskList>,
    onListClick: (TaskList) -> Unit,
    onNewClick: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(taskLists, key = { it.id }, contentType = { CONTENT_TYPE_LIST }) { taskList ->
            TaskListItem(Modifier.animateItemPlacement(), taskList, onListClick)
        }

        item(contentType = CONTENT_TYPE_CREATE) {
            CreateListItem(
                Modifier.animateItemPlacement(),
                onClick = onNewClick
            )
        }
    }
}

@Composable
fun TaskListItem(
    modifier: Modifier = Modifier,
    taskList: TaskList,
    onClick: (TaskList) -> Unit
) {
    ColorTheme(color = taskList.color) {
        Surface(
            color = MaterialTheme.colorScheme.themedContainer,
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp),
            onClick = { onClick(taskList) },
            tonalElevation = 4.dp,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Row(modifier = modifier.padding(vertical = 16.dp)) {
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically),
                    imageVector = taskList.icon.vector,
                    contentDescription = taskList.icon?.toString() ?: "List"
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = taskList.title,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    if (!taskList.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(taskList.description!!)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview_TaskListItem() {
    TaskListItem(
        taskList = TaskList(
            id = "1",
            title = "Tasks",
            description = "This is a very long description"
        ),
        onClick = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListItem(modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        enabled = enabled,
        onClick = { onClick() },
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically),
                imageVector = Icons.Filled.Add,
                contentDescription = "Add"
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    text = "New list",
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview_CreateListItem() {
    CreateListItem {}
}