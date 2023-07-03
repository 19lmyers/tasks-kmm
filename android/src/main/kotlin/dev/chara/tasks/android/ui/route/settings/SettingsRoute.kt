package dev.chara.tasks.android.ui.route.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chara.tasks.viewmodel.settings.SettingsViewModel

@Composable
fun SettingsRoute(
    navigateUp: () -> Unit
) {
    val viewModel: SettingsViewModel = viewModel()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (!state.value.firstLoad) {
        SettingsScreen(
            state.value,
            snackbarHostState,
            onUpClicked = {
                navigateUp()
            },
            onSetTheme = {
                viewModel.setAppTheme(it)
            },
            onSetThemeVariant = {
                viewModel.setAppThemeVariant(it)
            },
            onUpdateBoardSection = { boardSection, enabled ->
                viewModel.setEnabledForBoardSection(boardSection, enabled)
            },
            onUpdateList = {
                viewModel.updateList(it)
            }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    LaunchedEffect(viewModel.messages) {
        viewModel.messages.collect { message ->
            snackbarHostState.showSnackbar(
                message = message.text,
                duration = SnackbarDuration.Short,
                withDismissAction = true,
            )
        }
    }
}