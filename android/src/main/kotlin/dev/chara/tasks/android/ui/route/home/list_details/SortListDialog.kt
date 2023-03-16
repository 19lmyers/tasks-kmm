package dev.chara.tasks.android.ui.route.home.list_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chara.tasks.android.ui.component.util.MaterialDialog
import dev.chara.tasks.model.TaskList

@Composable
fun SortListDialog(
    sortType: TaskList.SortType,
    onDismiss: () -> Unit,
    onSelect: (TaskList.SortType) -> Unit
) {
    MaterialDialog(semanticTitle = "Sort by", onClose = onDismiss) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .selectableGroup()
        ) {
            Box(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "Sort by", style = MaterialTheme.typography.headlineSmall)
            }
            for (type in TaskList.SortType.values()) {
                ListItem(
                    modifier = Modifier.selectable(
                        selected = sortType == type,
                        onClick = {
                            onSelect(type)
                        },
                        role = Role.RadioButton
                    ),
                    headlineContent = {
                        Text(type.toString())
                    },
                    leadingContent = {
                        RadioButton(
                            selected = sortType == type,
                            onClick = null
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview_SortListDialog() {
    SortListDialog(
        sortType = TaskList.SortType.DATE_CREATED,
        onDismiss = {},
        onSelect = {}
    )
}