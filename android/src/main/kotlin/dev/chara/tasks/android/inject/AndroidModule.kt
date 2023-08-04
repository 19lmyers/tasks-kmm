package dev.chara.tasks.android.inject

import dev.chara.tasks.android.BuildConfig
import dev.chara.tasks.android.ext.AndroidFirebaseWrapper
import dev.chara.tasks.android.ext.AndroidWidgetManager
import dev.chara.tasks.shared.data.preference.DataStorePath
import dev.chara.tasks.shared.data.rest.Endpoint
import dev.chara.tasks.shared.database.DriverFactory
import dev.chara.tasks.shared.domain.FriendlyDateFormatter
import dev.chara.tasks.shared.ext.FirebaseWrapper
import dev.chara.tasks.shared.ext.WidgetManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun androidExt() = module {
    single<FirebaseWrapper> { AndroidFirebaseWrapper() }
    single<WidgetManager> { AndroidWidgetManager(androidContext()) }
}

fun androidDataLayer() = module {
    factory { DriverFactory(androidContext()) }
    factory { DataStorePath(androidContext()) }
    factory { Endpoint(BuildConfig.ENDPOINT_URL) }
}

fun androidUiLayer() = module {
    single { FriendlyDateFormatter(androidContext()) }
}