package dev.chara.tasks.shared.data.rest

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClient(config: HttpClientConfig<*>.() -> Unit) =
    HttpClient(Darwin) { config(this) }
