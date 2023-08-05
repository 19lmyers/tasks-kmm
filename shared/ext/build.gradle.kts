@file:Suppress("UNUSED_VARIABLE")

plugins {
    alias(libs.plugins.kotlin.multiplatform)

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
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "dev.chara.tasks.shared.ext"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}

ktfmt {
    kotlinLangStyle()
}