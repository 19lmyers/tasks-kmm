package dev.chara.tasks.android.notification.worker

import android.content.Context
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.chara.tasks.android.notification.service.MessagingService
import dev.chara.tasks.data.DataResult
import dev.chara.tasks.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CompleteTaskWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams), KoinComponent {

    private val repository by inject<Repository>()

    override suspend fun doWork(): Result {
        val taskId = inputData.getString(TASK_ID) ?: return Result.failure()

        val result = withContext(Dispatchers.IO) {
            val task =
                repository.getTaskById(taskId).first()
                    ?: return@withContext DataResult.Failure("No task found for id")

            repository.updateTask(
                task.listId, task.id, task.copy(
                    isCompleted = true,
                    lastModified = Clock.System.now()
                )
            )
        }

        return withContext(Dispatchers.Main) {
            NotificationManagerCompat.from(applicationContext)
                .cancel(taskId, MessagingService.NOTIFICATION_TYPE_REMINDER)

            result.fold(
                onSuccess = {
                    Toast.makeText(
                        applicationContext,
                        "Task marked as complete",
                        Toast.LENGTH_SHORT
                    ).show()
                    Result.success()
                },
                onFailure = {
                    Toast.makeText(
                        applicationContext,
                        it ?: "An unknown error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                    Result.failure()
                }
            )
        }
    }

    companion object {
        const val TASK_ID = "TASK_ID"
    }
}