package dev.chara.tasks.shared.ui.content.home.sheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chara.tasks.shared.component.home.join_list.JoinListComponent
import dev.chara.tasks.shared.ui.item.ProfileImage
import dev.chara.tasks.shared.ui.model.icon
import dev.chara.tasks.shared.ui.theme.ColorTheme
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHigh
import dev.chara.tasks.shared.ui.theme.extend.surfaceContainerHighest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinListContent(component: JoinListComponent) {
    val state = component.state.collectAsState()

    val scrollState = rememberScrollState()

    if (state.value.isLoading) return

    if (state.value.taskList == null) {
        AlertDialog(
            onDismissRequest = { component.onDismiss() },
            title = { Text("Invalid invite") },
            text = { Text("The invite was invalid, or may have expired.") },
            confirmButton = { TextButton(onClick = { component.onDismiss() }) { Text("Dismiss") } },
        )
        return
    }

    ColorTheme(color = state.value.taskList?.color) {
        ModalBottomSheet(
            onDismissRequest = { component.onDismiss() },
            windowInsets = WindowInsets.ime
        ) {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Text(
                    "Join list",
                    modifier = Modifier.padding(16.dp, 0.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                Surface(
                    modifier = Modifier.padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest)
                ) {
                    ListItem(
                        colors =
                            ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                        headlineContent = { Text(state.value.taskList?.title ?: "Task List") },
                        supportingContent = {
                            if (state.value.taskList?.description != null) {
                                Text(state.value.taskList?.description!!)
                            }
                        },
                        leadingContent = {
                            Icon(
                                state.value.taskList?.icon?.icon ?: Icons.Filled.Checklist,
                                contentDescription = null
                            )
                        }
                    )
                }

                val owner = state.value.owner ?: return@Column
                ListItem(
                    modifier = Modifier.padding(bottom = 48.dp),
                    headlineContent = { Text(owner.displayName) },
                    leadingContent = {
                        ProfileImage(
                            email = owner.email,
                            profilePhotoUri = owner.profilePhotoUri,
                            getGravatarUri = { component.getGravatarUri(it) },
                            modifier = Modifier.requiredSize(24.dp)
                        )
                    },
                    trailingContent = { Text("Owner") }
                )

                FilledTonalButton(
                    modifier =
                        Modifier.padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                            .align(Alignment.End),
                    onClick = { component.requestJoin() },
                    enabled = !state.value.isLoading
                ) {
                    Text("Join")
                }
            }
        }
    }
}
