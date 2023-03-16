package dev.chara.tasks.inject

import dev.chara.tasks.data.Repository
import dev.chara.tasks.data.cache.CacheDataSource
import dev.chara.tasks.data.preference.PreferenceDataSource
import dev.chara.tasks.data.rest.RestDataSource
import org.koin.dsl.module

fun commonDataLayer() = module {
    single { PreferenceDataSource(get()) }
    single { RestDataSource(get(), get()) }
    single { CacheDataSource(get()) }

    single { Repository(get(), get(), get()) }
}