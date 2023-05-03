package dev.chara.tasks.data.cache

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.chara.tasks.data.cache.sql.CacheDatabase
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory

actual class DriverFactory(private val context: Context) {
    actual fun create(): SqlDriver =
        AndroidSqliteDriver(
            CacheDatabase.Schema, context, "cache_v0.db",
            factory = RequerySQLiteOpenHelperFactory(),
            callback = CallbackDelegate(AndroidSqliteDriver.Callback(CacheDatabase.Schema))
        )
}

private class CallbackDelegate(
    val delegate: AndroidSqliteDriver.Callback,
) : SupportSQLiteOpenHelper.Callback(CacheDatabase.Schema.version) {
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