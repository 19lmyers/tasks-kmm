plugins {
    id("dev.chara.tasks.convention.plugin.android-library")

    alias(libs.plugins.kotlin.plugin.serialization)
}

kotlin {
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "dev.chara.tasks.shared.model"
}

ktfmt {
    kotlinLangStyle()
}