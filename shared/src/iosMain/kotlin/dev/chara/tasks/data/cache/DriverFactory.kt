package dev.chara.tasks.data.cache

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import dev.chara.tasks.data.cache.sql.CacheDatabase

private val dbConfig = DatabaseConfiguration(
    name = "cache.db",
    version = CacheDatabase.Schema.version,
    create = { connection ->
        wrapConnection(connection) { CacheDatabase.Schema.create(it) }
    },
    upgrade = { connection, oldVersion, newVersion ->
        wrapConnection(connection) { CacheDatabase.Schema.migrate(it, oldVersion, newVersion) }
    },
    extendedConfig = DatabaseConfiguration.Extended(
        foreignKeyConstraints = true
    )
)

actual class DriverFactory {
    actual fun create(): SqlDriver = NativeSqliteDriver(dbConfig)
}