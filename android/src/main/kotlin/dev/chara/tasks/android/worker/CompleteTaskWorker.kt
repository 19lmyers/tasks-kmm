package dev.chara.tasks.android.worker

import android.content.Context
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.mapError
import dev.chara.tasks.android.notification.service.MessagingService
import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.ext.WidgetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CompleteTaskWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams), KoinComponent {

    private val repository by inject<Repository>()
    private val widgetManager by inject<WidgetManager>()

    override suspend fun doWork(): Result {
        val taskId = inputData.getString(TASK_ID) ?: return Result.failure()

        val isCompleted = inputData.getBoolean(IS_COMPLETED, true)

        val result = withContext(Dispatchers.IO) {
            val task =
                repository.getTaskById(taskId).first()
                    ?: return@withContext Err("No task found for id")

            repository.updateTask(
                task.listId, task.id, task.copy(
                    isCompleted = isCompleted,
                    lastModified = Clock.System.now()
                )
            ).mapError {
                "TODO map this"
            }
        }

        return withContext(Dispatchers.Main) {
            NotificationManagerCompat.from(applicationContext)
                .cancel(taskId, MessagingService.NOTIFICATION_TYPE_REMINDER)

            widgetManager.update()

            result.mapBoth(
                success = {
                    Toast.makeText(
                        applicationContext,
                        "Task marked as complete",
                        Toast.LENGTH_SHORT
                    ).show()
                    Result.success()
                },
                failure = {
                    Toast.makeText(
                        applicationContext,
                        it,
                        Toast.LENGTH_SHORT
                    ).show()
                    Result.failure()
                }
            )
        }
    }

    companion object {
        const val TASK_ID = "TASK_ID"
        const val IS_COMPLETED = "IS_COMPLETED"
    }
}