package dev.chara.tasks.android.notification.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dev.chara.tasks.android.R
import dev.chara.tasks.android.model.drawable
import dev.chara.tasks.android.model.res
import dev.chara.tasks.android.notification.Action
import dev.chara.tasks.android.notification.receiver.NotificationActionReceiver
import dev.chara.tasks.android.ui.MainActivity
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.model.TaskList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MessagingService : FirebaseMessagingService(), LifecycleOwner {

    private val dispatcher = ServiceLifecycleDispatcher(this)

    override val lifecycle: Lifecycle = dispatcher.lifecycle

    private val repository: Repository by inject()

    override fun onCreate() {
        dispatcher.onServicePreSuperOnCreate()
        super.onCreate()
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onStart(intent: Intent, startId: Int) {
        dispatcher.onServicePreSuperOnStart()
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun onNewToken(token: String) {
        lifecycleScope.launch {
            if (!repository.isUserAuthenticated().first()) return@launch

            repository.linkFCMToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val messageType = message.data[DATA_MESSAGE_TYPE]

        if (messageType == MESSAGE_TYPE_REMINDER) {
            val taskId = message.data[DATA_TASK_ID] ?: return
            val taskLabel = message.data[DATA_TASK_LABEL] ?: return
            val listTitle = message.data[DATA_LIST_TITLE] ?: return

            val listColor =
                try {
                    TaskList.Color.valueOf(message.data[DATA_LIST_COLOR] ?: "")
                } catch (ex: IllegalArgumentException) {
                    null
                }
            val listIcon =
                try {
                    TaskList.Icon.valueOf(message.data[DATA_LIST_ICON] ?: "")
                } catch (ex: IllegalArgumentException) {
                    null
                }

            val reminderChannelId = getString(R.string.notification_channel_reminders)

            val editTaskIntent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.Builder()
                        .scheme("https")
                        .authority(MainActivity.DEEP_LINK_HOST)
                        .path(MainActivity.PATH_VIEW_TASK)
                        .appendQueryParameter(MainActivity.QUERY_TASK_ID, taskId)
                        .build()
                )

            val editTaskPendingIntent =
                PendingIntent.getActivity(
                    this,
                    0,
                    editTaskIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                )

            val completeTaskIntent =
                Intent(this, NotificationActionReceiver::class.java)
                    .setAction(NotificationActionReceiver.ACTION_COMPLETE_TASK)
                    .putExtra(NotificationActionReceiver.EXTRA_TASK_ID, taskId)

            val completeTaskPendingIntent =
                PendingIntent.getBroadcast(
                    this,
                    0,
                    completeTaskIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                )

            var builder =
                NotificationCompat.Builder(this, reminderChannelId)
                    .setSmallIcon(listIcon.drawable)
                    .setContentTitle(taskLabel)
                    .setContentText("Reminder")
                    .setSubText(listTitle)
                    .setShowWhen(true)
                    .setWhen(message.sentTime)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setContentIntent(editTaskPendingIntent)
                    .setAutoCancel(true)
                    .addAction(R.drawable.done_48px, "Mark as complete", completeTaskPendingIntent)

            if (listColor != null) {
                builder = builder.setColor(resources.getColor(listColor.res, theme))
            }

            if (
                ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val mChannel =
                        NotificationChannel(
                            reminderChannelId,
                            "Reminders",
                            NotificationManager.IMPORTANCE_DEFAULT
                        )

                    val notificationManager =
                        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(mChannel)
                }

                NotificationManagerCompat.from(this)
                    .notify(taskId, NOTIFICATION_TYPE_REMINDER, builder.build())
            }
        } else if (messageType == MESSAGE_TYPE_ACTION) {
            val taskId = message.data[DATA_TASK_ID]
            val taskLabel = message.data[DATA_TASK_LABEL]
            val taskCategory = message.data[DATA_TASK_CATEGORY]

            val listId = message.data[DATA_LIST_ID]
            val listTitle = message.data[DATA_LIST_TITLE]
            val listIcon =
                TaskList.Icon.entries.firstOrNull { it.name == message.data[DATA_LIST_ICON] }
            val listColor =
                TaskList.Color.entries.firstOrNull { it.name == message.data[DATA_LIST_COLOR] }

            val action = Action.valueOf(message.data[DATA_ACTION] ?: return)

            val actorId = message.data[DATA_ACTOR_ID]
            val actorName = message.data[DATA_ACTOR_NAME]

            lifecycleScope.launch {
                val userProfile = repository.getUserProfile().firstOrNull()

                repository.refresh()

                if (userProfile?.id == actorId) return@launch

                val body =
                    when (action) {
                        Action.ADD_TASK -> "$actorName added"
                        Action.EDIT_TASK -> "$actorName edited"
                        Action.REMOVE_TASK -> "$actorName removed"
                        Action.COMPLETE_TASK -> "$actorName completed"
                        Action.STAR_TASK -> "$actorName starred"
                        Action.PREDICT_TASK_CATEGORY -> "$taskLabel → $taskCategory"
                        Action.CLEAR_COMPLETED_TASKS -> "$actorName deleted all completed tasks"
                        Action.JOIN_LIST -> "$actorName joined"
                    }

                val actionChannelId = getString(R.string.notification_channel_actions)

                val viewListIntent =
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.Builder()
                            .scheme("https")
                            .authority(MainActivity.DEEP_LINK_HOST)
                            .path(MainActivity.PATH_VIEW_LIST)
                            .appendQueryParameter(MainActivity.QUERY_LIST_ID, listId)
                            .build()
                    )

                val viewListPendingIntent =
                    PendingIntent.getActivity(
                        this@MessagingService,
                        0,
                        viewListIntent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                    )

                var builder =
                    NotificationCompat.Builder(this@MessagingService, actionChannelId)
                        .setSmallIcon(listIcon.drawable)
                        .setContentTitle(action.getFriendlyName(taskLabel, actorName))
                        .setContentText(body)
                        .setSubText(listTitle)
                        .setShowWhen(true)
                        .setWhen(message.sentTime)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setContentIntent(viewListPendingIntent)
                        .setAutoCancel(true)
                        .setGroup(GROUP_ACTIONS)

                if (listColor != null) {
                    builder = builder.setColor(resources.getColor(listColor.res, theme))
                }

                if (
                    ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val mChannel =
                            NotificationChannel(
                                actionChannelId,
                                "Activity",
                                NotificationManager.IMPORTANCE_LOW
                            )

                        val notificationManager =
                            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.createNotificationChannel(mChannel)
                    }

                    NotificationManagerCompat.from(this@MessagingService)
                        .notify(taskId, NOTIFICATION_TYPE_ACTION, builder.build())

                    var summaryBuilder =
                        NotificationCompat.Builder(this@MessagingService, actionChannelId)
                            .setContentTitle("Shared list activity")
                            .setSubText(listTitle)
                            .setSmallIcon(listIcon.drawable)
                            .setGroup(GROUP_ACTIONS)
                            .setGroupSummary(true)

                    if (listColor != null) {
                        summaryBuilder =
                            summaryBuilder.setColor(resources.getColor(listColor.res, theme))
                    }

                    NotificationManagerCompat.from(this@MessagingService)
                        .notify("", NOTIFICATION_TYPE_ACTION, summaryBuilder.build())
                }
            }
        }
    }

    companion object {
        private const val DATA_MESSAGE_TYPE = "DATA_MESSAGE_TYPE"

        private const val MESSAGE_TYPE_REMINDER = "MESSAGE_TYPE_REMINDER"
        private const val MESSAGE_TYPE_ACTION = "MESSAGE_TYPE_ACTION"

        private const val DATA_TASK_ID = "DATA_TASK_ID"
        private const val DATA_TASK_LABEL = "DATA_TASK_LABEL"
        private const val DATA_TASK_CATEGORY = "DATA_TASK_CATEGORY"

        private const val DATA_LIST_ID = "DATA_LIST_ID"
        private const val DATA_LIST_TITLE = "DATA_LIST_TITLE"
        private const val DATA_LIST_COLOR = "DATA_LIST_COLOR"
        private const val DATA_LIST_ICON = "DATA_LIST_ICON"

        private const val DATA_ACTION = "DATA_ACTION"
        private const val DATA_ACTOR_ID = "DATA_ACTOR_ID"
        private const val DATA_ACTOR_NAME = "DATA_ACTOR_NAME"
        private const val DATA_ACTOR_PHOTO = "DATA_ACTOR_PHOTO"

        const val NOTIFICATION_TYPE_REMINDER = 1
        const val NOTIFICATION_TYPE_ACTION = 2

        const val GROUP_ACTIONS = "ACTIONS"
    }
}
