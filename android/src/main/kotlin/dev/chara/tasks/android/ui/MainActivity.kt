package dev.chara.tasks.android.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chara.tasks.android.notification.service.MessagingService
import dev.chara.tasks.android.ui.theme.AppTheme
import dev.chara.tasks.android.ui.viewmodel.LocalViewModelStore
import dev.chara.tasks.android.ui.viewmodel.viewModel
import dev.chara.tasks.model.Theme
import dev.chara.tasks.viewmodel.BaseViewModel
import dev.chara.tasks.viewmodel.ViewModelStore
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navController

class MainActivity : FragmentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val initialBackstack: List<RootNavTarget> = parseDeepLinks()

        setContent {
            val coroutineScope = rememberCoroutineScope()

            val viewModelStore = remember(coroutineScope) { ViewModelStore(coroutineScope) }

            val presenter = viewModelStore.viewModel("Base") { scope ->
                BaseViewModel(scope)
            }

            val userTheme by presenter.getAppTheme()
                .collectAsStateWithLifecycle(initialValue = Theme.SYSTEM_DEFAULT)

            val isDarkTheme = when (userTheme) {
                Theme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                Theme.LIGHT -> false
                Theme.DARK -> true
            }

            val vibrantColors by presenter.useVibrantColors()
                .collectAsStateWithLifecycle(initialValue = false)

            val navController: NavController<RootNavTarget> = rememberSaveable(initialBackstack) {
                navController(initialBackstack = initialBackstack)
            }

            val windowSizeClass = calculateWindowSizeClass(this)

            AppTheme(darkTheme = isDarkTheme, vibrantColors = vibrantColors) {
                CompositionLocalProvider(LocalViewModelStore provides viewModelStore) {
                    RootNavHost(navController, windowSizeClass = windowSizeClass)
                }
            }
        }
    }

    private fun parseDeepLinks(): List<RootNavTarget> =
        if (intent.data != null && intent.data!!.host == DEEP_LINK_HOST) {

            when (intent.data!!.path) {
                PATH_RESET_PASSWORD -> {
                    val token = intent.data!!.getQueryParameter(QUERY_PASSWORD_RESET_TOKEN)

                    if (token != null) {
                        listOf(RootNavTarget.ResetPassword(token))
                    } else {
                        Toast.makeText(this, "Missing token parameter", Toast.LENGTH_LONG).show()
                        listOf(RootNavTarget.Home.Default)
                    }
                }

                PATH_VIEW_LIST -> {
                    val listId = intent.data!!.getQueryParameter(QUERY_LIST_ID)

                    if (listId != null) {
                        listOf(RootNavTarget.Home.WithList(listId))
                    } else {
                        Toast.makeText(this, "Missing id parameter", Toast.LENGTH_LONG).show()
                        listOf(RootNavTarget.Home.Default)
                    }
                }

                PATH_VIEW_TASK -> {
                    val taskId = intent.data!!.getQueryParameter(QUERY_TASK_ID)

                    if (taskId != null) {
                        NotificationManagerCompat.from(this)
                            .cancel(taskId, MessagingService.NOTIFICATION_TYPE_REMINDER)

                        listOf(RootNavTarget.Home.WithTask(taskId))
                    } else {
                        Toast.makeText(this, "Missing id parameter", Toast.LENGTH_LONG).show()
                        listOf(RootNavTarget.Home.Default)
                    }
                }

                else -> {
                    listOf(RootNavTarget.Home.Default)
                }
            }
        } else {
            listOf(RootNavTarget.Home.Default)
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
