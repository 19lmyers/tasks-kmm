package dev.chara.tasks.shared.ui.content.home.sheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import dev.chara.tasks.shared.component.home.modify_list.ModifyListComponent
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.ui.model.icon
import dev.chara.tasks.shared.ui.model.seed
import dev.chara.tasks.shared.ui.theme.ColorTheme
import dev.chara.tasks.shared.ui.theme.LocalDarkTheme
import dev.chara.tasks.shared.ui.theme.LocalThemeVariant
import dev.chara.tasks.shared.ui.theme.color.style
import dev.chara.tasks.shared.ui.theme.extend.dynamicSurfaceColors
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHigh
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHighest
import kotlinx.datetime.Clock

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ModifyListContent(component: ModifyListComponent) {
    val state = component.state.collectAsState()

    var showIconDialog by remember { mutableStateOf(false) }

    var showClassifierDialog by remember { mutableStateOf(false) }

    var listTitle by remember(state.value) { mutableStateOf(state.value.selectedList?.title ?: "") }

    var listColor by remember(state.value) { mutableStateOf(state.value.selectedList?.color) }
    var listIcon by remember(state.value) { mutableStateOf(state.value.selectedList?.icon) }
    var description by
        remember(state.value) { mutableStateOf(state.value.selectedList?.description) }

    var classifierType by
        remember(state.value) { mutableStateOf(state.value.selectedList?.classifierType) }

    var showIndexNumbers by
        remember(state.value) {
            mutableStateOf(state.value.selectedList?.showIndexNumbers ?: false)
        }

    ColorTheme(color = listColor) {
        if (showIconDialog) {
            AlertDialog(
                onDismissRequest = { showIconDialog = false },
            ) {
                Surface(
                    modifier = Modifier.padding(24.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest),
                    tonalElevation = 6.0.dp,
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Box(modifier = Modifier.padding(bottom = 16.dp)) {
                            Text(
                                text = "Select icon",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        LazyVerticalGrid(
                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                            columns = GridCells.Adaptive(56.dp)
                        ) {
                            item {
                                IconSwatch(
                                    icon = Icons.Filled.Checklist,
                                    contentDescription = "Checklist",
                                    selected = listIcon == null
                                ) {
                                    listIcon = null
                                    showIconDialog = false
                                }
                            }

                            items(TaskList.Icon.entries.toTypedArray(), key = { it.name }) { icon ->
                                IconSwatch(
                                    icon = icon.icon,
                                    contentDescription = icon.toString(),
                                    selected = listIcon == icon
                                ) {
                                    listIcon = icon
                                    showIconDialog = false
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showClassifierDialog) {
            AlertDialog(
                onDismissRequest = { showClassifierDialog = false },
                icon = {
                    Icon(
                        Icons.Filled.Category,
                        contentDescription = null,
                    )
                },
                title = { Text("Categorize (preview)") },
                text = {
                    Column {
                        Card(
                            colors =
                                CardDefaults.cardColors(
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(24.dp)
                                ),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            onClick = {
                                classifierType =
                                    if (classifierType == null) TaskList.ClassifierType.SHOPPING
                                    else null
                            }
                        ) {
                            Box(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                                Row(
                                    modifier =
                                        Modifier.padding(horizontal = 16.dp)
                                            .align(Alignment.CenterStart)
                                ) {
                                    Icon(
                                        Icons.Filled.ShoppingCart,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier =
                                            Modifier.padding(end = 16.dp)
                                                .align(Alignment.CenterVertically)
                                    )
                                    Text(
                                        "Grocery",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                    )
                                }
                                Switch(
                                    checked = classifierType == TaskList.ClassifierType.SHOPPING,
                                    onCheckedChange = {
                                        classifierType =
                                            if (it) TaskList.ClassifierType.SHOPPING else null
                                    },
                                    modifier =
                                        Modifier.align(Alignment.CenterEnd).padding(end = 16.dp)
                                )
                            }
                        }
                        Text(
                            """
                            List categorization uses Google's Gemini AI to semantically categorize your list.
                            
                            To accomplish this, some task labels may be sent to Google servers.
                            
                            Currently only grocery lists are supported.
                            """
                                .trimIndent()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showClassifierDialog = false }) { Text("Done") }
                },
            )
        }

        ModalBottomSheet(
            onDismissRequest = { component.onDismiss() },
            windowInsets = WindowInsets.ime
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusRequester = remember { FocusRequester() }

            val scrollState = rememberScrollState()

            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Text(
                    if (state.value.selectedList?.id.isNullOrBlank()) "Create list"
                    else "Edit list",
                    modifier = Modifier.padding(16.dp, 0.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                if (state.value.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                }

                OutlinedTextField(
                    modifier =
                        Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                    label = { Text(text = "Title") },
                    value = listTitle,
                    onValueChange = { listTitle = it },
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done,
                        ),
                    keyboardActions =
                        KeyboardActions(
                            onDone = {
                                if (listTitle.isNotBlank()) {
                                    keyboardController?.hide()
                                }
                            }
                        )
                )

                ListItem(
                    modifier = Modifier.clickable { showIconDialog = true },
                    headlineContent = { Text(text = "Icon") },
                    trailingContent = {
                        Icon(imageVector = listIcon.icon, contentDescription = "List")
                    }
                )

                val outlineColor = MaterialTheme.colorScheme.outline
                val selectionColor = MaterialTheme.colorScheme.primary

                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()
                ) {
                    item {
                        ColorSwatch(
                            color = null,
                            outline = outlineColor,
                            selection = selectionColor,
                            selected = listColor == null
                        ) {
                            listColor = null
                        }
                    }

                    items(items = TaskList.Color.entries.toTypedArray(), key = { it.name }) { color
                        ->
                        ColorTheme(color = color) {
                            ColorSwatch(
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                outline = outlineColor,
                                selection = selectionColor,
                                selected = listColor == color
                            ) {
                                listColor = color
                            }
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                    label = { Text(text = "Description") },
                    value = description ?: "",
                    onValueChange = { description = it.ifBlank { null } },
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                        ),
                    trailingIcon = {
                        IconButton(onClick = { description = null }) {
                            Icon(imageVector = Icons.Filled.Clear, "Clear")
                        }
                    }
                )

                ListItem(
                    modifier = Modifier.clickable { showClassifierDialog = true },
                    headlineContent = {
                        Text(
                            text = "Categorize (preview)",
                        )
                    },
                    leadingContent = {
                        Icon(Icons.Filled.Category, contentDescription = "Categorize")
                    },
                    trailingContent = {
                        InputChip(
                            label = { Text(text = classifierType?.toString() ?: "Off") },
                            selected = classifierType != null,
                            onClick = { showClassifierDialog = true }
                        )
                    }
                )

                ListItem(
                    headlineContent = { Text(text = "Show list numbers") },
                    leadingContent = {
                        Icon(imageVector = Icons.Filled.FormatListNumbered, "List numbers")
                    },
                    trailingContent = {
                        Switch(
                            checked = showIndexNumbers,
                            onCheckedChange = { showIndexNumbers = it }
                        )
                    }
                )

                FilledTonalButton(
                    modifier =
                        Modifier.padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                            .align(Alignment.End),
                    onClick = {
                        keyboardController?.hide()

                        component.onSave(
                            if (state.value.selectedList != null) {
                                state.value.selectedList!!.copy(
                                    title = listTitle,
                                    color = listColor,
                                    icon = listIcon,
                                    description = description,
                                    showIndexNumbers = showIndexNumbers,
                                    classifierType = classifierType,
                                    lastModified = Clock.System.now()
                                )
                            } else {
                                TaskList(
                                    id = "",
                                    title = listTitle,
                                    color = listColor,
                                    icon = listIcon,
                                    description = description,
                                    showIndexNumbers = showIndexNumbers,
                                    classifierType = classifierType,
                                    lastModified = Clock.System.now()
                                )
                            }
                        )
                    },
                    enabled = listTitle.isNotBlank()
                ) {
                    Text("Save")
                }

                LaunchedEffect(focusRequester) {
                    if (listTitle.isEmpty()) {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSwatch(
    color: Color?,
    outline: Color,
    selection: Color,
    selected: Boolean,
    onSelected: () -> Unit,
) {
    val modifier =
        if (color != null) {
            Modifier.background(color, MaterialTheme.shapes.medium)
        } else {
            val darkTheme = LocalDarkTheme.current
            val variant = LocalThemeVariant.current

            Modifier.background(
                Brush.sweepGradient(
                    TaskList.Color.entries.map { listColor ->
                        val surfaceColors =
                            dynamicSurfaceColors(listColor.seed, darkTheme, variant.style)
                        surfaceColors.surfaceContainerHigh
                    }
                ),
                MaterialTheme.shapes.medium
            )
        }

    Surface(
        modifier = Modifier.requiredSize(72.dp).padding(8.dp).then(modifier),
        shape = MaterialTheme.shapes.medium,
        color = Color.Transparent,
        border =
            BorderStroke(
                2.dp,
                if (selected) {
                    selection
                } else {
                    outline
                }
            )
    ) {
        Box(modifier = Modifier.clickable { onSelected() }) {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.align(Alignment.Center),
                    tint = selection
                )
            }
        }
    }
}

@Composable
fun IconSwatch(
    icon: ImageVector,
    contentDescription: String,
    selected: Boolean,
    onSelected: () -> Unit,
) {
    Surface(
        modifier = Modifier.requiredSize(72.dp).padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border =
            BorderStroke(
                2.dp,
                if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )
    ) {
        Box(modifier = Modifier.clickable { onSelected() }) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.align(Alignment.Center),
                tint =
                    if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
            )
        }
    }
}
