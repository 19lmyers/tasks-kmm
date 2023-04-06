package dev.chara.tasks.android.inject

import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dev.chara.tasks.android.BuildConfig
import dev.chara.tasks.data.cache.DriverFactory
import dev.chara.tasks.data.preference.DataStorePath
import dev.chara.tasks.data.rest.Endpoint
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun androidDataLayer() = module {
    factory { DriverFactory(androidContext()) }
    factory { DataStorePath(androidContext()) }
    factory { Endpoint(BuildConfig.ENDPOINT_URL) }
}

fun androidUiLayer() = module {
    single {
        ImageLoader.Builder(androidContext())
            .memoryCache {
                MemoryCache.Builder(androidContext())
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(androidContext().cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }
}