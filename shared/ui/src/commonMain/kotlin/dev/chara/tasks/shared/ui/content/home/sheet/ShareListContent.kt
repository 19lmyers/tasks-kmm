package dev.chara.tasks.shared.ui.content.home.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.Ok
import dev.chara.tasks.shared.component.home.share_list.ShareListComponent
import dev.chara.tasks.shared.model.Profile
import dev.chara.tasks.shared.ui.item.ProfileImage
import dev.chara.tasks.shared.ui.theme.ColorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareListContent(component: ShareListComponent) {
    val state = component.state.collectAsState()

    val scrollState = rememberScrollState()

    var showRemoveMemberDialogFor by remember { mutableStateOf<Profile?>(null) }

    if (state.value.isLoading) return

    var showShareSheetFor by remember { mutableStateOf<String?>(null) }

    // TODO more elegant API for share sheets?
    if (showShareSheetFor != null) {
        ShareText(
            "I've shared a list with you! Join at: https://tasks.chara.dev/join?token=$showShareSheetFor"
        )
        showShareSheetFor = null
    }

    ColorTheme(color = state.value.list?.color) {
        if (showRemoveMemberDialogFor != null) {
            AlertDialog(
                onDismissRequest = { showRemoveMemberDialogFor = null },
                title = { Text("Remove ${showRemoveMemberDialogFor!!.displayName}?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            component.removeMember(showRemoveMemberDialogFor!!.id)
                            showRemoveMemberDialogFor = null
                            component.onDismiss()
                        }
                    ) {
                        Text("Remove")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRemoveMemberDialogFor = null }) { Text("Cancel") }
                }
            )
        }

        ModalBottomSheet(
            onDismissRequest = { component.onDismiss() },
            windowInsets = WindowInsets.ime
        ) {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Text(
                    "Share list",
                    modifier = Modifier.padding(16.dp, 0.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                val owner = state.value.owner ?: return@Column
                ListItem(
                    modifier = Modifier.padding(top = 16.dp),
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

                for (member in state.value.members) {
                    ListItem(
                        headlineContent = { Text(member.displayName) },
                        leadingContent = {
                            ProfileImage(
                                email = member.email,
                                profilePhotoUri = member.profilePhotoUri,
                                getGravatarUri = { component.getGravatarUri(it) },
                                modifier = Modifier.requiredSize(24.dp)
                            )
                        },
                        trailingContent = {
                            if (state.value.profile?.id == state.value.owner?.id) {
                                IconButton(onClick = { showRemoveMemberDialogFor = member }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Remove member")
                                }
                            }
                        }
                    )
                }

                ListItem(
                    headlineContent = { Text("Add member") },
                    leadingContent = {
                        Icon(Icons.Filled.PersonAdd, contentDescription = "Add member")
                    },
                    modifier =
                        Modifier.padding(bottom = 48.dp).clickable {
                            component.requestShare { result ->
                                if (result is Ok) {
                                    showShareSheetFor = result.value
                                } else {
                                    // TODO show error
                                }
                            }
                        }
                )
            }
        }
    }
}

@Composable expect fun ShareText(text: String)
