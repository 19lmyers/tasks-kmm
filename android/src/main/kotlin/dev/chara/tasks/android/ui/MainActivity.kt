package dev.chara.tasks.android.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chara.tasks.android.notification.service.MessagingService
import dev.chara.tasks.android.ui.theme.AppTheme
import dev.chara.tasks.model.preference.Theme
import dev.chara.tasks.viewmodel.BaseViewModel
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navController

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val initialBackstack: List<NavTarget> = parseDeepLinks()

        setContent {
            val viewModel: BaseViewModel = viewModel()

            val state = viewModel.uiState.collectAsStateWithLifecycle()

            val isDarkTheme = when (state.value.appTheme) {
                Theme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                Theme.LIGHT -> false
                Theme.DARK -> true
            }

            val navController: NavController<NavTarget> = rememberSaveable(initialBackstack) {
                navController(initialBackstack = initialBackstack)
            }

            val windowSizeClass = calculateWindowSizeClass(this)

            AppTheme(darkTheme = isDarkTheme, vibrantColors = state.value.useVibrantColors) {
                RootNavHost(navController, windowSizeClass = windowSizeClass)
            }
        }
    }

    private fun parseDeepLinks(): List<NavTarget> =
        if (intent.data != null && intent.data!!.host == DEEP_LINK_HOST) {

            when (intent.data!!.path) {
                PATH_RESET_PASSWORD -> {
                    val token = intent.data!!.getQueryParameter(QUERY_PASSWORD_RESET_TOKEN)

                    if (token != null) {
                        listOf(NavTarget.ResetPassword(token))
                    } else {
                        Toast.makeText(this, "Missing token parameter", Toast.LENGTH_LONG).show()
                        listOf(NavTarget.Home.Default)
                    }
                }

                PATH_VIEW_LIST -> {
                    val listId = intent.data!!.getQueryParameter(QUERY_LIST_ID)

                    if (listId != null) {
                        listOf(NavTarget.Home.WithList(listId))
                    } else {
                        Toast.makeText(this, "Missing id parameter", Toast.LENGTH_LONG).show()
                        listOf(NavTarget.Home.Default)
                    }
                }

                PATH_VIEW_TASK -> {
                    val taskId = intent.data!!.getQueryParameter(QUERY_TASK_ID)

                    if (taskId != null) {
                        NotificationManagerCompat.from(this)
                            .cancel(taskId, MessagingService.NOTIFICATION_TYPE_REMINDER)

                        listOf(NavTarget.Home.WithTask(taskId))
                    } else {
                        Toast.makeText(this, "Missing id parameter", Toast.LENGTH_LONG).show()
                        listOf(NavTarget.Home.Default)
                    }
                }

                else -> {
                    listOf(NavTarget.Home.Default)
                }
            }
        } else {
            listOf(NavTarget.Home.Default)
        }

    companion object {
        const val DEEP_LINK_HOST = "tasks.chara.dev"

        const val PATH_RESET_PASSWORD = "/reset"
        const val QUERY_PASSWORD_RESET_TOKEN = "token"

        const val PATH_VIEW_LIST = "/list"
        const val QUERY_LIST_ID = "id"

        const val PATH_VIEW_TASK = "/task"
        const val QUERY_TASK_ID = "id"
    }
}
