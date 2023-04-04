package dev.chara.tasks.android.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chara.tasks.android.ui.component.util.MaterialDialog
import dev.chara.tasks.android.ui.route.CreateTaskDialog
import dev.chara.tasks.android.ui.theme.AppTheme
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.Theme
import dev.chara.tasks.viewmodel.shortcut.ShortcutUiState
import dev.chara.tasks.viewmodel.shortcut.ShortcutViewModel
import kotlinx.datetime.Clock

class ShortcutActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val viewModel: ShortcutViewModel by viewModel()

            val state by viewModel.uiState.collectAsStateWithLifecycle()

            val userTheme by viewModel.getAppTheme()
                .collectAsStateWithLifecycle(initialValue = Theme.SYSTEM_DEFAULT)

            val isDarkTheme = when (userTheme) {
                Theme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                Theme.LIGHT -> false
                Theme.DARK -> true
            }

            val vibrantColors by viewModel.useVibrantColors()
                .collectAsStateWithLifecycle(initialValue = false)

            AppTheme(darkTheme = isDarkTheme, vibrantColors = vibrantColors) {
                if (state is ShortcutUiState.Authenticated) {
                    val loadedState = state as ShortcutUiState.Authenticated

                    var creationInProgress by remember { mutableStateOf(false) }

                    if (creationInProgress) {
                        MaterialDialog(
                            semanticTitle = "Loading...",
                            onClose = {}
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    } else {
                        CreateTaskDialog(
                            taskLists = loadedState.taskLists,
                            current = Task(
                                id = "",
                                listId = loadedState.taskLists.first().id,
                                label = "",
                                lastModified = Clock.System.now()
                            ),
                            onDismiss = {
                                finish()
                            },
                            onSave = { task ->
                                viewModel.createTask(task.listId, task)
                                creationInProgress = true
                            }
                        )
                    }
                } else if (state is ShortcutUiState.NotAuthenticated) {
                    Toast.makeText(this, "Log in to create tasks", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            LaunchedEffect(viewModel.statuses) {
                viewModel.statuses.collect { status ->
                    if (status.first) {
                        Toast.makeText(this@ShortcutActivity, "Task created", Toast.LENGTH_LONG)
                            .show()
                        finish()
                    } else {
                        Toast.makeText(this@ShortcutActivity, status.second, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }
}