package dev.chara.tasks.android

import android.app.Application
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import dev.chara.tasks.android.inject.androidDataLayer
import dev.chara.tasks.android.inject.androidExt
import dev.chara.tasks.android.inject.androidUiLayer
import dev.chara.tasks.shared.data.inject.sharedDataLayer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class TasksApp : Application() {
    @OptIn(ExperimentalKermitApi::class)
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@TasksApp)

            modules(androidExt(), androidDataLayer(), sharedDataLayer(), androidUiLayer())
        }

        Logger.addLogWriter(CrashlyticsLogWriter())
    }
}
