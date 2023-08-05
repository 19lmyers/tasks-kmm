package dev.chara.tasks.android.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.defaultComponentContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.chara.tasks.android.notification.service.MessagingService
import dev.chara.tasks.shared.component.DeepLink
import dev.chara.tasks.shared.component.DefaultRootComponent
import dev.chara.tasks.shared.model.preference.Theme
import dev.chara.tasks.shared.ui.content.RootContent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val root =
            DefaultRootComponent(
                componentContext = defaultComponentContext(),
                deepLink = parseIntent()
            )

        setContent {
            RootContent(component = root)

            val state = root.state.collectAsState()

            val darkTheme =
                when (state.value.appTheme) {
                    Theme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                    Theme.LIGHT -> false
                    Theme.DARK -> true
                }

            val systemUiController = rememberSystemUiController()
            DisposableEffect(systemUiController, darkTheme) {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = !darkTheme
                )

                onDispose {}
            }
        }
    }

    private fun parseIntent(): DeepLink =
        if (intent.data != null && intent.data!!.host == DEEP_LINK_HOST) {
            when (intent.data!!.path) {
                PATH_VERIFY_EMAIL -> {
                    val token = intent.data!!.getQueryParameter(QUERY_EMAIL_VERIFY_TOKEN)

                    if (token != null) {
                        DeepLink.VerifyEmail(token)
                    } else {
                        Toast.makeText(this, "Missing token parameter", Toast.LENGTH_LONG).show()
                        DeepLink.None
                    }
                }
                PATH_RESET_PASSWORD -> {
                    val token = intent.data!!.getQueryParameter(QUERY_PASSWORD_RESET_TOKEN)

                    if (token != null) {
                        DeepLink.ResetPassword(token)
                    } else {
                        Toast.makeText(this, "Missing token parameter", Toast.LENGTH_LONG).show()
                        DeepLink.None
                    }
                }
                PATH_VIEW_LIST -> {
                    val listId = intent.data!!.getQueryParameter(QUERY_LIST_ID)

                    if (listId != null) {
                        DeepLink.ViewList(listId)
                    } else {
                        Toast.makeText(this, "Missing id parameter", Toast.LENGTH_LONG).show()
                        DeepLink.None
                    }
                }
                PATH_VIEW_TASK -> {
                    val taskId = intent.data!!.getQueryParameter(QUERY_TASK_ID)

                    if (taskId != null) {
                        NotificationManagerCompat.from(this)
                            .cancel(taskId, MessagingService.NOTIFICATION_TYPE_REMINDER)

                        DeepLink.ViewTask(taskId)
                    } else {
                        Toast.makeText(this, "Missing id parameter", Toast.LENGTH_LONG).show()
                        DeepLink.None
                    }
                }
                else -> {
                    DeepLink.None
                }
            }
        } else if (intent.action == ACTION_NEW_TASK) {
            DeepLink.CreateTask
        } else {
            DeepLink.None
        }

    companion object {
        const val DEEP_LINK_HOST = "tasks.chara.dev"

        const val PATH_VERIFY_EMAIL = "/verify"
        const val QUERY_EMAIL_VERIFY_TOKEN = "token"

        const val PATH_RESET_PASSWORD = "/reset"
        const val QUERY_PASSWORD_RESET_TOKEN = "token"

        const val PATH_VIEW_LIST = "/list"
        const val QUERY_LIST_ID = "id"

        const val PATH_VIEW_TASK = "/task"
        const val QUERY_TASK_ID = "id"

        const val ACTION_NEW_TASK = "dev.chara.tasks.android.ACTION_NEW_TASK"
    }
}
