package dev.chara.tasks.android.ui.component.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chara.tasks.android.model.vector
import dev.chara.tasks.android.ui.theme.ColorTheme
import dev.chara.tasks.android.ui.theme.LocalThemeColor
import dev.chara.tasks.model.TaskList
import kotlinx.datetime.Clock

@Deprecated("replace with bottom sheet / split into 2")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ModifyListDialog(
    title: String,
    current: TaskList,
    onDismiss: () -> Unit,
    onSave: (TaskList) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var showIconGrid by remember { mutableStateOf(false) }

    var listTitle by remember { mutableStateOf(current.title) }

    var listColor by remember { mutableStateOf(current.color) }
    var listIcon by remember { mutableStateOf(current.icon) }
    var description by remember { mutableStateOf(current.description) }

    var isPinned by remember { mutableStateOf(current.isPinned) }
    var showIndexNumbers by remember { mutableStateOf(current.showIndexNumbers) }

    ColorTheme(color = listColor) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = title)
            },
            icon = {
                IconButton(onClick = { showIconGrid = !showIconGrid }) {
                    Icon(
                        imageVector = listIcon.vector,
                        contentDescription = "Icon"
                    )
                }
            },
            text = {
                if (showIconGrid) {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        columns = GridCells.Adaptive(56.dp)
                    ) {
                        item {
                            IconSwatch(
                                icon = Icons.Filled.Checklist,
                                contentDescription = "Checklist",
                                selected = listIcon == null
                            ) {
                                listIcon = null
                            }
                        }

                        items(TaskList.Icon.values(), key = { it.name }) { icon ->
                            IconSwatch(
                                icon = icon.vector,
                                contentDescription = icon.toString(),
                                selected = listIcon == icon
                            ) {
                                listIcon = icon
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            label = { Text(text = "Title") },
                            value = listTitle,
                            onValueChange = { listTitle = it },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (listTitle.isNotBlank()) {
                                        keyboardController?.hide()
                                    }
                                }
                            ),
                            trailingIcon = {
                                IconButton(onClick = { listTitle = "" }) {
                                    Icon(imageVector = Icons.Filled.Clear, "Clear")
                                }
                            }
                        )

                        val outlineColor = MaterialTheme.colorScheme.outline
                        val selectionColor = MaterialTheme.colorScheme.primary

                        LazyRow(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth()
                        ) {
                            item {
                                ColorSwatch(
                                    color = LocalThemeColor.current,
                                    outline = outlineColor,
                                    selection = selectionColor,
                                    selected = listColor == null
                                ) {
                                    listColor = null
                                }
                            }

                            items(
                                items = TaskList.Color.values(),
                                key = { it.name }
                            ) { color ->
                                ColorTheme(color = color) {
                                    ColorSwatch(
                                        color = MaterialTheme.colorScheme.primaryContainer,
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
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth(),
                            label = { Text(text = "Description") },
                            value = description ?: "",
                            onValueChange = {
                                description = it.ifBlank { null }
                            },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                            ),
                            trailingIcon = {
                                IconButton(onClick = { description = null }) {
                                    Icon(imageVector = Icons.Filled.Clear, "Clear")
                                }
                            }
                        )

                        ListItem(
                            headlineContent = { Text(text = "Pin to dashboard") },
                            leadingContent = {
                                Icon(imageVector = Icons.Filled.PushPin, "Pin")
                            },
                            trailingContent = {
                                Switch(
                                    checked = isPinned,
                                    onCheckedChange = {
                                        isPinned = it
                                    }
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
                                    onCheckedChange = {
                                        showIndexNumbers = it
                                    }
                                )
                            }
                        )

                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        keyboardController?.hide()
                        onSave(
                            current.copy(
                                title = listTitle,
                                color = listColor,
                                icon = listIcon,
                                description = description,
                                isPinned = isPinned,
                                showIndexNumbers = showIndexNumbers,
                                lastModified = Clock.System.now()
                            )
                        )
                    },
                    enabled = listTitle.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        )
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}

@Composable
fun ColorSwatch(
    color: Color,
    outline: Color,
    selection: Color,
    selected: Boolean,
    onSelected: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .requiredSize(72.dp)
            .padding(8.dp),
        color = color,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            2.dp, if (selected) {
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
        modifier = Modifier
            .requiredSize(72.dp)
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            2.dp, if (selected) {
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
                tint = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )
        }
    }
}

@Preview
@Composable
private fun Preview_ModifyListDialog() {
    ModifyListDialog(
        title = "Edit list",
        current = TaskList(id = "1", title = "Tasks"),
        onDismiss = {},
        onSave = {}
    )
}