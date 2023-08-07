package dev.chara.tasks.shared.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import dev.chara.tasks.shared.database.sql.SQLDatabase

private val dbConfig =
    DatabaseConfiguration(
        name = "cache_v2.db",
        version = SQLDatabase.Schema.version.toInt(),
        create = { connection -> wrapConnection(connection) { SQLDatabase.Schema.create(it) } },
        upgrade = { connection, oldVersion, newVersion ->
            wrapConnection(connection) {
                SQLDatabase.Schema.migrate(it, oldVersion.toLong(), newVersion.toLong())
            }
        },
        extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true)
    )

actual class DriverFactory {
    actual operator fun invoke(): SqlDriver = NativeSqliteDriver(dbConfig)
}
