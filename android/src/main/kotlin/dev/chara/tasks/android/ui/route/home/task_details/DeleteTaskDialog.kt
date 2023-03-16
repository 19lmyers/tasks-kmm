package dev.chara.tasks.android.ui.route.home.task_details

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DeleteTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete item?") },
        text = {
            Text(text = "This item will be permanently deleted")
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
private fun Preview_DeleteTaskDialog() {
    DeleteTaskDialog(
        onDismiss = {},
        onConfirm = {}
    )
}