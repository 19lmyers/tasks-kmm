package dev.chara.tasks.android.ui.component.util

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun MaterialDialog(
    semanticTitle: String,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = modifier.then(
                Modifier
                    .semantics { paneTitle = semanticTitle }
                    .padding(24.dp)
            ),
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            contentColor = AlertDialogDefaults.textContentColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            content()
        }
    }
}