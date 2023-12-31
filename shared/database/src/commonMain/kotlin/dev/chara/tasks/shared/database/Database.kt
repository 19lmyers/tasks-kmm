package dev.chara.tasks.shared.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import dev.chara.tasks.shared.database.sql.SQLDatabase
import dev.chara.tasks.shared.database.sql.Task
import dev.chara.tasks.shared.database.sql.TaskList
import dev.chara.tasks.shared.database.sql.TaskListPrefs
import kotlinx.datetime.Instant

expect class DriverFactory {
    operator fun invoke(): SqlDriver
}

private val instantAdapter =
    object : ColumnAdapter<Instant, String> {
        override fun decode(databaseValue: String): Instant = Instant.parse(databaseValue)

        override fun encode(value: Instant): String = value.toString()
    }

object Database {
    operator fun invoke(driverFactory: DriverFactory): SQLDatabase {
        val driver = driverFactory()

        return SQLDatabase(
            driver,
            Task.Adapter(
                reminder_dateAdapter = instantAdapter,
                due_dateAdapter = instantAdapter,
                date_createdAdapter = instantAdapter,
                last_modifiedAdapter = instantAdapter
            ),
            TaskList.Adapter(
                colorAdapter = EnumColumnAdapter(),
                iconAdapter = EnumColumnAdapter(),
                date_createdAdapter = instantAdapter,
                last_modifiedAdapter = instantAdapter,
                classifier_typeAdapter = EnumColumnAdapter()
            ),
            TaskListPrefs.Adapter(
                sort_typeAdapter = EnumColumnAdapter(),
                sort_directionAdapter = EnumColumnAdapter(),
                last_modifiedAdapter = instantAdapter
            )
        )
    }
}
