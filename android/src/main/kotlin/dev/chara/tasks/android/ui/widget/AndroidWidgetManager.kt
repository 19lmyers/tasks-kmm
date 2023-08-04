package dev.chara.tasks.android.ui.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import dev.chara.tasks.shared.ext.WidgetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidWidgetManager(private val applicationContext: Context) : WidgetManager {
    override suspend fun update() {
        withContext(Dispatchers.Main) {
            TaskListWidget().updateAll(applicationContext)
        }
    }
}