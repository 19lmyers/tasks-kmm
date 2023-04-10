package dev.chara.tasks

import cocoapods.FirebaseCore.FIRApp
import dev.chara.tasks.data.cache.DriverFactory
import dev.chara.tasks.data.preference.DataStorePath
import dev.chara.tasks.data.rest.Endpoint
import dev.chara.tasks.inject.commonDataLayer
import dev.chara.tasks.logging.CrashlyticsAntilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.dsl.module

private fun iosDataLayer(endpointUrl: String) = module {
    factory { DriverFactory() }
    factory { DataStorePath() }
    factory { Endpoint(endpointUrl) }
}

fun initKoin(endpointUrl: String) {
    startKoin {
        modules(commonDataLayer(), iosDataLayer(endpointUrl))
    }
}

fun initFirebase() {
    FIRApp.configure()
}

fun initNapierDebug() {
    Napier.base(DebugAntilog())
}

fun initNapierRelease() {
    Napier.base(CrashlyticsAntilog())
}