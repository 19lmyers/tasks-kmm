@file:Suppress("unused")

package dev.chara.tasks.framework

import androidx.compose.ui.window.ComposeUIViewController
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import dev.chara.tasks.shared.component.RootComponent
import dev.chara.tasks.shared.data.inject.sharedDataLayer
import dev.chara.tasks.shared.data.preference.DataStorePath
import dev.chara.tasks.shared.data.rest.Endpoint
import dev.chara.tasks.shared.database.DriverFactory
import dev.chara.tasks.shared.domain.FriendlyDateFormatter
import dev.chara.tasks.shared.ext.FirebaseWrapper
import dev.chara.tasks.shared.ext.WidgetManager
import dev.chara.tasks.shared.ui.content.RootContent
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun setupKoin(firebaseWrapper: FirebaseWrapper, endpointUrl: String) {
    val appleExt = module {
        single<FirebaseWrapper> { firebaseWrapper }
        single<WidgetManager> {
            object : WidgetManager {
                override suspend fun update() {
                    // no-op
                }
            }
        }
    }

    val appleShared = module {
        factory { DriverFactory() }
        factory { DataStorePath() }
        factory { Endpoint(endpointUrl) }
    }

    val appleUi = module { single { FriendlyDateFormatter() } }

    startKoin { modules(appleExt, sharedDataLayer(), appleShared, appleUi) }
}

@OptIn(ExperimentalKermitApi::class)
fun setupLogger() {
    Logger.addLogWriter(
        CrashlyticsLogWriter(
            minSeverity = Severity.Info,
            minCrashSeverity = Severity.Warn,
        )
    )
    setCrashlyticsUnhandledExceptionHook()
}

fun mainViewController(rootComponent: RootComponent) = ComposeUIViewController {
    RootContent(component = rootComponent)
}
