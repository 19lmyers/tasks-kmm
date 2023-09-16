package dev.chara.tasks.shared.ui.content.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.chara.tasks.shared.component.settings.SettingsComponent
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.board.BoardSection
import dev.chara.tasks.shared.model.preference.Theme
import dev.chara.tasks.shared.model.preference.ThemeVariant
import dev.chara.tasks.shared.ui.model.icon
import org.burnoutcrew.reorderable.polyfill.ReorderableItem
import org.burnoutcrew.reorderable.polyfill.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.polyfill.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.polyfill.reorderable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsContent(component: SettingsComponent) {
    val state = component.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = { component.onUp() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Navigate up",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->
            val paddingTop = PaddingValues(top = innerPadding.calculateTopPadding())
            val paddingBottom = PaddingValues(bottom = innerPadding.calculateBottomPadding())

            BoardSettings(
                modifier =
                    Modifier.padding(paddingTop)
                        .consumeWindowInsets(paddingTop)
                        .fillMaxSize()
                        .padding(paddingBottom),
                appTheme = state.value.appTheme,
                appThemeVariant = state.value.appThemeVariant,
                enabledBoardSections = state.value.enabledBoardSections,
                taskLists = state.value.taskLists,
                onSetTheme = { component.setAppTheme(it) },
                onSetThemeVariant = { component.setAppThemeVariant(it) },
                onUpdateBoardSection = { section, enabled ->
                    component.setEnabledForBoardSection(section, enabled)
                },
                onReorderList = { listId, fromIndex, toIndex ->
                    component.reorderList(listId, fromIndex, toIndex)
                },
            )
        }
    )

    LaunchedEffect(component.messages) {
        component.messages.collect { message ->
            snackbarHostState.showSnackbar(
                message = message.text,
                duration = SnackbarDuration.Short,
                withDismissAction = true,
            )
        }
    }
}

private const val KEY_HEADER = "KEY_HEADER"

private const val CONTENT_TYPE_LIST = "CONTENT_TYPE_LIST"
private const val CONTENT_TYPE_HEADER = "CONTENT_TYPE_HEADER"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun BoardSettings(
    modifier: Modifier = Modifier,
    appTheme: Theme,
    appThemeVariant: ThemeVariant,
    enabledBoardSections: List<BoardSection.Type>,
    taskLists: List<TaskList>,
    onSetTheme: (Theme) -> Unit,
    onSetThemeVariant: (ThemeVariant) -> Unit,
    onUpdateBoardSection: (BoardSection.Type, Boolean) -> Unit,
    onReorderList: (String, Int, Int) -> Unit
) {
    var showThemeDropdown by remember { mutableStateOf(false) }

    var showThemeVariantDropdown by remember { mutableStateOf(false) }

    var reorderableLists = remember { taskLists.toMutableStateList() }

    SideEffect {
        reorderableLists.apply {
            clear()
            addAll(taskLists)
        }
    }

    val reorderableState =
        rememberReorderableLazyListState(
            onMove = { from, to ->
                // Subtract the header item
                val fromIndex = from.index - 1
                val toIndex = to.index - 1

                reorderableLists = reorderableLists.apply { add(toIndex, removeAt(fromIndex)) }
                onReorderList(from.key as String, fromIndex, toIndex)
            },
            canDragOver = { from, _ -> from.index > 0 }
        )

    LazyColumn(
        state = reorderableState.listState,
        modifier = modifier.reorderable(reorderableState)
    ) {
        item(key = KEY_HEADER, contentType = CONTENT_TYPE_HEADER) {
            ListItem(
                headlineContent = {
                    Text(text = "Appearance", style = MaterialTheme.typography.titleMedium)
                }
            )

            ListItem(
                modifier = Modifier.clickable { showThemeDropdown = true },
                headlineContent = {
                    Text(
                        text = "Theme",
                    )
                },
                trailingContent = {
                    ExposedDropdownMenuBox(
                        expanded = showThemeDropdown,
                        onExpandedChange = { showThemeDropdown = it }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor().width(200.dp),
                            readOnly = true,
                            value = appTheme.toString(),
                            onValueChange = {},
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = showThemeDropdown
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                        DropdownMenu(
                            modifier = Modifier.exposedDropdownSize(true),
                            expanded = showThemeDropdown,
                            onDismissRequest = { showThemeDropdown = false },
                        ) {
                            for (theme in Theme.entries) {
                                DropdownMenuItem(
                                    text = { Text(theme.toString()) },
                                    onClick = {
                                        onSetTheme(theme)
                                        showThemeDropdown = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
            )

            ListItem(
                modifier = Modifier.clickable { showThemeVariantDropdown = true },
                headlineContent = {
                    Text(
                        text = "Color scheme",
                    )
                },
                trailingContent = {
                    ExposedDropdownMenuBox(
                        expanded = showThemeVariantDropdown,
                        onExpandedChange = { showThemeVariantDropdown = it }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor().width(200.dp),
                            readOnly = true,
                            value = appThemeVariant.toString(),
                            onValueChange = {},
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = showThemeVariantDropdown
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                        DropdownMenu(
                            modifier = Modifier.exposedDropdownSize(true),
                            expanded = showThemeVariantDropdown,
                            onDismissRequest = { showThemeVariantDropdown = false },
                        ) {
                            for (variant in ThemeVariant.entries) {
                                DropdownMenuItem(
                                    text = { Text(variant.toString()) },
                                    onClick = {
                                        onSetThemeVariant(variant)
                                        showThemeVariantDropdown = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
            )

            ListItem(
                headlineContent = {
                    Text(text = "Dashboard sections", style = MaterialTheme.typography.titleMedium)
                }
            )

            for (boardSection in BoardSection.Type.entries) {
                ListItem(
                    modifier =
                        Modifier.clickable {
                            onUpdateBoardSection(
                                boardSection,
                                !enabledBoardSections.contains(boardSection)
                            )
                        },
                    headlineContent = { Text(boardSection.title) },
                    leadingContent = { Icon(boardSection.icon, contentDescription = "List") },
                    trailingContent = {
                        Switch(
                            checked = enabledBoardSections.contains(boardSection),
                            onCheckedChange = { onUpdateBoardSection(boardSection, it) }
                        )
                    }
                )
            }

            ListItem(
                headlineContent = {
                    Text(text = "Task lists", style = MaterialTheme.typography.titleMedium)
                },
                supportingContent = {
                    Text(
                        text = "Drag to reorder",
                    )
                }
            )
        }

        items(reorderableLists, key = { it.id }, contentType = { CONTENT_TYPE_LIST }) { list ->
            ReorderableItem(reorderableState, key = list.id) { isDragging ->
                val shadowElevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)

                ListItem(
                    modifier =
                        Modifier.animateItemPlacement()
                            .detectReorderAfterLongPress(reorderableState)
                            .zIndex(zIndex = if (isDragging) 1f else 0f),
                    headlineContent = { Text(list.title) },
                    leadingContent = { Icon(list.icon.icon, contentDescription = "List") },
                    trailingContent = {
                        Icon(Icons.Filled.DragHandle, contentDescription = "Drag to reorder")
                    },
                    shadowElevation = shadowElevation
                )
            }
        }
    }
}
