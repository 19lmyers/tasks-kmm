package dev.chara.tasks.android.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import dev.chara.tasks.android.worker.CompleteTaskWorker

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_COMPLETE_TASK -> {
                val taskId = intent.getStringExtra(EXTRA_TASK_ID)

                val completeTaskRequest: WorkRequest =
                    OneTimeWorkRequestBuilder<CompleteTaskWorker>()
                        .setInputData(workDataOf(CompleteTaskWorker.TASK_ID to taskId))
                        .build()

                WorkManager.getInstance(context).enqueue(completeTaskRequest)
            }
        }
    }

    companion object {
        const val ACTION_COMPLETE_TASK = "dev.chara.tasks.android.ACTION_COMPLETE_TASK"
        const val EXTRA_TASK_ID = "dev.chara.tasks.android.EXTRA_TASK_ID"
    }
}
