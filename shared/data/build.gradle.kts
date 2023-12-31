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
            implementation(project(":shared:model"))

            implementation(project(":shared:database"))
            implementation(project(":shared:ext"))

            implementation(libs.datastore)

            implementation(libs.kermit)

            implementation(libs.koin.core)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.result)
            implementation(libs.result.coroutines)

            implementation(libs.sqldelight.coroutines.extensions)

            implementation(libs.uuid)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "dev.chara.tasks.shared.data"
}