package dev.chara.tasks.android.ui.route.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chara.tasks.viewmodel.settings.SettingsUiState
import dev.chara.tasks.viewmodel.settings.SettingsViewModel

@Composable
fun SettingsRoute(
    presenter: SettingsViewModel,
    navigateUp: () -> Unit
) {
    val state = presenter.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (state.value is SettingsUiState.Loaded) {
        SettingsScreen(
            state.value as SettingsUiState.Loaded,
            snackbarHostState,
            onUpClicked = {
                navigateUp()
            },
            onSetTheme = {
                presenter.setAppTheme(it)
            },
            onSetStartScreen = {
                presenter.setStartScreen(it)
            },
            onSetVibrantColors = {
                presenter.setVibrantColors(it)
            },
            onUpdateBoardSection = { boardSection, enabled ->
                presenter.setEnabledForBoardSection(boardSection, enabled)
            },
            onUpdateList = {
                presenter.updateList(it)
            }
        )
    } else {
        Surface(modifier = Modifier.fillMaxSize()) {
            // Placeholder
        }
    }

    LaunchedEffect(presenter.messages) {
        presenter.messages.collect { message ->
            snackbarHostState.showSnackbar(
                message = message.text,
                duration = SnackbarDuration.Short,
                withDismissAction = true,
            )
        }
    }
}