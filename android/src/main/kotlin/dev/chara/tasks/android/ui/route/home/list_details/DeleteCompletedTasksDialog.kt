package dev.chara.tasks.android.ui.route.home.list_details

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DeleteCompletedTasksDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete all completed tasks?") },
        text = {
            Text(text = "Completed tasks will be permanently deleted from this list")
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
private fun Preview_DeleteCompletedTasksDialog() {
    DeleteCompletedTasksDialog(
        onDismiss = {},
        onConfirm = {}
    )
}