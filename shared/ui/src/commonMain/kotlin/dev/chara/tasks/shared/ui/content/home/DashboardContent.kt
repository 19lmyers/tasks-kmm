package dev.chara.tasks.shared.ui.content.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chara.tasks.shared.component.home.dashboard.DashboardComponent
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.ui.item.TaskItem
import dev.chara.tasks.shared.ui.model.color
import dev.chara.tasks.shared.ui.model.icon
import dev.chara.tasks.shared.ui.theme.ColorTheme
import dev.chara.tasks.shared.ui.theme.DynamicTheme
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainer
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHigh
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHighest
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerLow
import kotlinx.coroutines.launch

private const val CONTENT_TYPE_HEADER = "CONTENT_TYPE_HEADER"
private const val CONTENT_TYPE_SECTION = "CONTENT_TYPE_SECTION"
private const val CONTENT_TYPE_LIST = "CONTENT_TYPE_LIST"
private const val CONTENT_TYPE_CREATE = "CONTENT_TYPE_CREATE"

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun DashboardContent(
    component: DashboardComponent,
    paddingTop: PaddingValues,
    paddingBottom: PaddingValues,
    nestedScrollConnection: NestedScrollConnection,
    snackbarHostState: SnackbarHostState
) {
    val state = component.state.collectAsState()

    val listState = rememberLazyListState()

    val pullRefreshState =
        rememberPullRefreshState(state.value.isRefreshing, { component.onRefresh() })

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier =
            Modifier.padding(paddingTop)
                .consumeWindowInsets(paddingTop)
                .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection),
            state = listState,
            contentPadding = paddingBottom
        ) {
            if (state.value.boardSections.isNotEmpty()) {
                stickyHeader(key = "board", contentType = CONTENT_TYPE_HEADER) {
                    val elevation: Dp by
                        animateDpAsState(
                            targetValue = if (listState.firstVisibleItemIndex > 0) 3.dp else 0.dp,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                        )

                    SectionHeader(title = "Dashboard", elevation = elevation) {
                        coroutineScope.launch { listState.animateScrollToItem(0) }
                    }
                }
            }

            items(
                state.value.boardSections,
                key = { section -> "board/${section.type.name}" },
                contentType = { CONTENT_TYPE_SECTION }
            ) { section ->
                BoardSectionCard(
                    title = section.type.title,
                    icon = section.type.icon,
                    tintColor = section.type.color
                ) {
                    for (task in section.tasks) {
                        val parentList = state.value.allLists.first { it.id == task.listId }

                        ColorTheme(color = parentList.color) {
                            TaskItem(
                                modifier = Modifier.padding(horizontal = 4.dp),
                                task = task,
                                parentList = parentList,
                                onListClicked = { component.onListClicked(it) },
                                onClick = { component.onTaskClicked(it) },
                                onUpdate = { component.updateTask(it) }
                            )
                        }
                    }
                }
            }

            if (state.value.boardSections.isNotEmpty()) {
                stickyHeader(key = "list", contentType = CONTENT_TYPE_HEADER) {
                    val elevation: Dp by
                        animateDpAsState(
                            targetValue =
                                if (
                                    listState.firstVisibleItemIndex > state.value.boardSections.size
                                ) {
                                    3.dp
                                } else {
                                    0.dp
                                },
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                        )

                    SectionHeader(title = "Lists", elevation = elevation) {
                        coroutineScope.launch {
                            listState.animateScrollToItem(state.value.boardSections.size + 1)
                        }
                    }
                }
            }

            items(
                state.value.boardLists,
                key = { list -> "list/${list.taskList.id}" },
                contentType = { CONTENT_TYPE_LIST }
            ) { boardList ->
                ListCard(
                    title = boardList.taskList.title,
                    icon = boardList.taskList.icon.icon,
                    description = boardList.taskList.description,
                    tintColor = boardList.taskList.color,
                    onClick = { component.onListClicked(boardList.taskList) }
                ) {
                    for ((index, task) in boardList.topTasks.withIndex()) {
                        TaskItem(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            task = task,
                            onClick = { component.onTaskClicked(it) },
                            onUpdate = { component.updateTask(it) },
                            showIndexNumbers = boardList.taskList.showIndexNumbers,
                            indexNumber = index + 1
                        )
                    }

                    val currentLeft = boardList.currentTaskCount - boardList.topTasks.size

                    if (boardList.currentTaskCount - boardList.topTasks.size > 0) {
                        ViewMore("View $currentLeft more") {
                            component.onListClicked(boardList.taskList)
                        }
                    } else if (boardList.completedTaskCount > 0) {
                        ViewMore("Completed (${boardList.completedTaskCount})") {
                            component.onListClicked(boardList.taskList)
                        }
                    } else if (boardList.topTasks.isEmpty()) {
                        Text(
                            "All tasks complete!",
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item(key = "create", contentType = CONTENT_TYPE_CREATE) {
                CreateListItem(
                    Modifier.animateItemPlacement()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    onClick = { component.onCreateList() }
                )

                Spacer(modifier = Modifier.padding(40.dp))
            }
        }

        PullRefreshIndicator(
            state.value.isRefreshing,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.primary
        )

        ExtendedFloatingActionButton(
            text = { Text(text = "New task") },
            icon = { Icon(Icons.Filled.Add, contentDescription = "New task") },
            onClick = { component.onCreateTask() },
            expanded = !listState.canScrollBackward,
            modifier =
                Modifier.align(Alignment.BottomEnd)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .padding(paddingBottom)
        )
    }

    LaunchedEffect(component.messages) {
        component.messages.collect { message ->
            snackbarHostState
                .showSnackbar(
                    message = message.text,
                    duration = SnackbarDuration.Short,
                    withDismissAction = message.action == null,
                    actionLabel = message.action?.text
                )
                .let {
                    if (it == SnackbarResult.ActionPerformed) {
                        message.action?.function?.invoke()
                    }
                }
        }
    }
}

@Composable
private fun SectionHeader(title: String, elevation: Dp, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.height(64.dp).fillMaxWidth(),
        tonalElevation = elevation,
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp).align(Alignment.CenterStart),
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.BoardSectionCard(
    title: String,
    icon: ImageVector,
    tintColor: Color,
    content: @Composable ColumnScope.() -> Unit,
) {
    DynamicTheme(seed = tintColor) {
        BoardSectionCardSurface(
            modifier =
                Modifier.animateItemPlacement()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            CardContent(title = title, icon = icon, content = content)
        }
    }
}

@Composable
private fun BoardSectionCardSurface(
    modifier: Modifier = Modifier,
    shape: Shape,
    content: @Composable () -> Unit
) {
    Box(
        modifier =
            modifier
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceContainerLow,
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = shape
                )
    ) {
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.ListCard(
    title: String,
    icon: ImageVector,
    description: String? = null,
    tintColor: TaskList.Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    ColorTheme(color = tintColor) {
        ListCardSurface(
            modifier =
                Modifier.animateItemPlacement()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            onClick = onClick
        ) {
            CardContent(
                title = title,
                icon = icon,
                description = description,
                onClick = onClick,
                content = content
            )
        }
    }
}

@Composable
private fun ListCardSurface(
    modifier: Modifier = Modifier,
    shape: Shape,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit
) {
    if (onClick != null) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = modifier,
            shape = shape,
            onClick = onClick,
            border =
                BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                )
        ) {
            content()
        }
    } else {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = modifier,
            shape = shape,
            border =
                BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                )
        ) {
            content()
        }
    }
}

@Composable
private fun CardContent(
    title: String,
    icon: ImageVector,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)) {
        Box(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
            Row {
                Icon(
                    modifier = Modifier.padding(8.dp).align(Alignment.CenterVertically),
                    imageVector = icon,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            if (onClick != null) {
                IconButton(
                    onClick = onClick,
                    modifier = Modifier.align(Alignment.TopEnd),
                ) {
                    Icon(
                        Icons.Filled.OpenInNew,
                        contentDescription = "View list",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        if (description != null) {
            Text(
                modifier =
                    Modifier.padding(horizontal = 8.dp).padding(start = 16.dp, bottom = 8.dp),
                text = description,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        content()
    }
}

@Composable
private fun ViewMore(text: String, onClick: () -> Unit) {
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge)
                .clickable { onClick() }
                .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListItem(modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedCard(
        modifier = modifier,
        onClick = { onClick() },
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Icon(
                modifier = Modifier.padding(8.dp).align(Alignment.CenterVertically),
                imageVector = Icons.Filled.Add,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.padding(16.dp).align(Alignment.CenterVertically)) {
                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    text = "New list",
                )
            }
        }
    }
}
