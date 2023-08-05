package dev.chara.tasks.shared.ui.content.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.androidx.material3.polyfill.DropdownMenu
import com.androidx.material3.polyfill.DropdownMenuItem
import com.androidx.material3.polyfill.ExposedDropdownMenuBox
import com.androidx.material3.polyfill.ExposedDropdownMenuDefaults
import dev.chara.tasks.shared.component.settings.SettingsComponent
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.board.BoardSection
import dev.chara.tasks.shared.model.preference.Theme
import dev.chara.tasks.shared.model.preference.ThemeVariant
import dev.chara.tasks.shared.ui.model.icon

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsContent(component: SettingsComponent) {
    val state = component.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val scrollState = rememberScrollState()

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
                        .verticalScroll(scrollState)
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
                onUpdateList = { component.updateList(it) }
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

@OptIn(ExperimentalMaterial3Api::class)
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
    onUpdateList: (TaskList) -> Unit
) {
    Column(modifier = modifier) {
        ListItem(
            headlineContent = {
                Text(text = "Appearance", style = MaterialTheme.typography.titleMedium)
            }
        )

        var showThemeDropdown by remember { mutableStateOf(false) }

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
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showThemeDropdown)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    DropdownMenu(
                        modifier = Modifier.exposedDropdownSize(true),
                        expanded = showThemeDropdown,
                        onDismissRequest = { showThemeDropdown = false },
                    ) {
                        for (theme in Theme.values()) {
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

        var showThemeVariantDropdown by remember { mutableStateOf(false) }

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
                        for (variant in ThemeVariant.values()) {
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

        for (boardSection in BoardSection.Type.values()) {
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
                    text = "Drag to reorder (coming soon)",
                )
            }
        )

        // TODO reorderable lists w/ ordinals

        for (taskList in taskLists) {
            ListItem(
                headlineContent = { Text(taskList.title) },
                leadingContent = { Icon(taskList.icon.icon, contentDescription = "List") },
                trailingContent = {
                    // Icon(Icons.Filled.DragHandle, contentDescription = "Drag to reorder")
                }
            )
        }
    }
}
