plugins {
    alias(libs.plugins.kotlin.plugin.serialization) apply false
    alias(libs.plugins.kotlin.atomicfu) apply false

    alias(libs.plugins.sqldelight) apply false

    alias(libs.plugins.jetbrains.compose) apply false

    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false

    alias(libs.plugins.versions)
}