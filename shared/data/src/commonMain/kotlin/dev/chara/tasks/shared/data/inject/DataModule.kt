package dev.chara.tasks.shared.data.inject

import dev.chara.tasks.shared.data.Repository
import dev.chara.tasks.shared.data.cache.CacheDataSource
import dev.chara.tasks.shared.data.preference.PreferenceDataSource
import dev.chara.tasks.shared.data.rest.RestDataSource
import org.koin.dsl.module

fun sharedDataLayer() = module {
    single { PreferenceDataSource(get()) }
    single { RestDataSource(get(), get()) }
    single { CacheDataSource(get()) }

    single { Repository(get(), get(), get(), get(), get()) }
}
