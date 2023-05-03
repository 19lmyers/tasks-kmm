package dev.chara.tasks.android.ui.route.home.task_details

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ConfirmExitDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Close without saving?") },
        text = {
            Text(text = "Your changes to this task will not be saved")
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Close")
            }
        }
    )
}

@Preview
@Composable
private fun Preview_ConfirmExitDialog() {
    ConfirmExitDialog(
        onDismiss = {},
        onConfirm = {}
    )
}