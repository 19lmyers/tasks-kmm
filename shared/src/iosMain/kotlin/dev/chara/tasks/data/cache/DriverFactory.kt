package dev.chara.tasks.data.cache

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import dev.chara.tasks.data.cache.sql.CacheDatabase

private val dbConfig = DatabaseConfiguration(
    name = "cache_v0.db",
    version = CacheDatabase.Schema.version.toInt(),
    create = { connection ->
        wrapConnection(connection) { CacheDatabase.Schema.create(it) }
    },
    upgrade = { connection, oldVersion, newVersion ->
        wrapConnection(connection) { CacheDatabase.Schema.migrate(it, oldVersion.toLong(), newVersion.toLong()) }
    },
    extendedConfig = DatabaseConfiguration.Extended(
        foreignKeyConstraints = true
    )
)

actual class DriverFactory {
    actual fun create(): SqlDriver = NativeSqliteDriver(dbConfig)
}
