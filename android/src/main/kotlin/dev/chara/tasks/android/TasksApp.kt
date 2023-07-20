package dev.chara.tasks.android

import android.app.Application
import dev.chara.tasks.android.firebase.AndroidFirebaseWrapper
import dev.chara.tasks.android.inject.androidDataLayer
import dev.chara.tasks.android.inject.androidUiLayer
import dev.chara.tasks.firebase.Firebase
import dev.chara.tasks.inject.commonDataLayer
import dev.chara.tasks.logging.CrashlyticsAntilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class TasksApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Firebase.link(AndroidFirebaseWrapper())

        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        } else {
            Napier.base(CrashlyticsAntilog())
        }

        startKoin {
            androidLogger()
            androidContext(this@TasksApp)

            modules(androidUiLayer(), androidDataLayer(), commonDataLayer())
        }
    }
}