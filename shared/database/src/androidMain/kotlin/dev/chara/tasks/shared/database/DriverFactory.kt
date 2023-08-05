package dev.chara.tasks.shared.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.chara.tasks.shared.database.sql.SQLDatabase
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory

actual class DriverFactory(private val context: Context) {
    actual operator fun invoke(): SqlDriver =
        AndroidSqliteDriver(
            SQLDatabase.Schema,
            context,
            "cache_v1.db",
            factory = RequerySQLiteOpenHelperFactory(),
            callback = CallbackDelegate(AndroidSqliteDriver.Callback(SQLDatabase.Schema))
        )
}

private class CallbackDelegate(
    val delegate: AndroidSqliteDriver.Callback,
) : SupportSQLiteOpenHelper.Callback(SQLDatabase.Schema.version.toInt()) {
    override fun onConfigure(db: SupportSQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        delegate.onOpen(db)
    }

    override fun onCreate(db: SupportSQLiteDatabase) {
        delegate.onCreate(db)
    }

    override fun onUpgrade(
        db: SupportSQLiteDatabase,
        oldVersion: Int,
        newVersion: Int,
    ) {
        delegate.onUpgrade(db, oldVersion, newVersion)
    }

    override fun onDowngrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
        delegate.onUpgrade(db, oldVersion, newVersion)
    }
}
