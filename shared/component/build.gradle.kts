@file:Suppress("UNUSED_VARIABLE")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)

    alias(libs.plugins.android.library)

    id("kotlin-parcelize")

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
                implementation(project(":shared:model"))

                implementation(project(":shared:data"))
                implementation(project(":shared:domain"))

                implementation(libs.kermit)

                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.koin.core)

                implementation(libs.decompose)

                implementation(libs.result)
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
    namespace = "dev.chara.tasks.shared.component"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}

ktfmt {
    kotlinLangStyle()
}