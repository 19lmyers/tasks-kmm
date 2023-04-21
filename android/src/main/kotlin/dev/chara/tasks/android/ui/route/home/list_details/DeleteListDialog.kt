package dev.chara.tasks.android.ui.route.home.list_details

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DeleteListDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete list?") },
        text = {
            Text(text = "All tasks in this list will be permanently deleted")
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        }
    )
}

@Preview
@Composable
private fun Preview_DeleteListDialog() {
    DeleteListDialog(
        onDismiss = {},
        onConfirm = {}
    )
}