package dev.chara.tasks.shared.ui.content.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.chara.tasks.shared.component.home.dashboard.DashboardComponent
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.ui.item.TaskItem
import dev.chara.tasks.shared.ui.model.color
import dev.chara.tasks.shared.ui.model.icon
import dev.chara.tasks.shared.ui.theme.ColorTheme
import dev.chara.tasks.shared.ui.theme.DynamicTheme
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainer
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHighest
import kotlinx.coroutines.launch

private const val CONTENT_TYPE_BOARD_SECTION = "CONTENT_TYPE_SECTION"
private const val CONTENT_TYPE_LIST = "CONTENT_TYPE_LIST"
private const val CONTENT_TYPE_CREATE = "CONTENT_TYPE_CREATE"

private const val TAB_INDEX_DASHBOARD = 0
private const val TAB_INDEX_LISTS = 1

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun DashboardContent(
    component: DashboardComponent,
    paddingTop: PaddingValues,
    paddingBottom: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
    snackbarHostState: SnackbarHostState
) {
    val state = component.state.collectAsState()

    val pagerState =
        rememberPagerState(
            pageCount = {
                if (state.value.boardSections.isNotEmpty()) {
                    2
                } else {
                    1
                }
            }
        )

    val pullRefreshState =
        rememberPullRefreshState(state.value.isRefreshing, { component.onRefresh() })

    val titles = listOf("Dashboard", "Lists")

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.padding(paddingTop).consumeWindowInsets(paddingTop)) {
        Column {
            if (state.value.boardSections.isNotEmpty()) {
                // Match the top app bar's color transitions
                val colorTransitionFraction = scrollBehavior.state.overlappedFraction
                val fraction = if (colorTransitionFraction > 0.01f) 1f else 0f
                val appBarContainerColor by
                    animateColorAsState(
                        targetValue =
                            lerp(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                                FastOutLinearInEasing.transform(fraction)
                            ),
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )

                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = appBarContainerColor,
                ) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            },
                            text = {
                                Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                        )
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                contentPadding = paddingBottom,
                beyondBoundsPageCount = 1
            ) { page ->
                Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
                    LazyColumn(
                        modifier =
                            Modifier.fillMaxSize()
                                .nestedScroll(scrollBehavior.nestedScrollConnection),
                        contentPadding = paddingBottom
                    ) {
                        if (state.value.boardSections.isNotEmpty() && page == TAB_INDEX_DASHBOARD) {
                            items(
                                state.value.boardSections,
                                key = { section -> "board/${section.type.name}" },
                                contentType = { CONTENT_TYPE_BOARD_SECTION }
                            ) { section ->
                                BoardSectionCard(
                                    title = section.type.title,
                                    icon = section.type.icon,
                                    tintColor = section.type.color
                                ) {
                                    for (task in section.tasks) {
                                        val parentList =
                                            state.value.allLists.first { it.id == task.listId }

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
                        } else {
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
                                            showIndexNumbers = boardList.prefs.showIndexNumbers,
                                            indexNumber = index + 1
                                        )
                                    }

                                    val currentLeft =
                                        boardList.currentTaskCount - boardList.topTasks.size

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

                            item(contentType = CONTENT_TYPE_CREATE) {
                                CreateListItem(
                                    Modifier.animateItemPlacement()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .fillMaxWidth(),
                                    onClick = { component.onCreateList() }
                                )

                                Spacer(modifier = Modifier.padding(40.dp))
                            }
                        }
                    }

                    PullRefreshIndicator(
                        state.value.isRefreshing,
                        pullRefreshState,
                        Modifier.align(Alignment.TopCenter),
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            text = { Text(text = "New task") },
            icon = { Icon(Icons.Filled.Add, contentDescription = "New task") },
            onClick = { component.onCreateTask() },
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.BoardSectionCard(
    title: String,
    icon: ImageVector,
    tintColor: Color,
    content: @Composable ColumnScope.() -> Unit,
) {
    DynamicTheme(seed = tintColor) {
        Surface(
            modifier =
                Modifier.animateItemPlacement()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = MaterialTheme.shapes.extraLarge,
            border =
                BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                )
        ) {
            CardContent(title = title, icon = icon, content = content)
        }
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
            CardContent(title = title, icon = icon, description = description, content = content)
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
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)) {
        Row(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
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
