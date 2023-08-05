@file:Suppress("UNUSED_VARIABLE")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)

    alias(libs.plugins.android.library)

    alias(libs.plugins.ktfmt)
}

kotlin {
    jvmToolchain(17)

    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "dev.chara.tasks.shared.model"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}

ktfmt {
    kotlinLangStyle()
}